package com.vnpt.intership.news.api.v1.service.impl;

import com.vnpt.intership.news.api.v1.config.security.JwtProvider;
import com.vnpt.intership.news.api.v1.domain.dto.EmailDTO;
import com.vnpt.intership.news.api.v1.domain.dto.response.TokenRefreshResponse;
import com.vnpt.intership.news.api.v1.domain.entity.UserEntity;
import com.vnpt.intership.news.api.v1.event.OnClearOtpResetPasswordEvent;
import com.vnpt.intership.news.api.v1.exception.OTPException;
import com.vnpt.intership.news.api.v1.exception.UserNotFoundException;
import com.vnpt.intership.news.api.v1.repository.CustomUserRepository;
import com.vnpt.intership.news.api.v1.repository.UserRepository;
import com.vnpt.intership.news.api.v1.service.MailService;
import com.vnpt.intership.news.api.v1.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${security.jwt.token.expirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${prefixOtp}")
    private String prefixOtp;

    @Value("${prefixOtpCountHit}")
    private String prefixOtpCountHit;

    @Override
    public void sendOtp(String username) {
        try {
            // check whether username have existed
            UserEntity userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("Username with: " + username + " not found!"));

            Integer otp = generateOtp();
            String subject = "Confirmation Reset of Yours Password in the platform News";
            EmailDTO emailDTO = EmailDTO.builder().recipients(List.of(userEntity.getEmail()))
                    .subject(subject).body(String.valueOf(otp)).build();

            // set context otp into template mail
            final Context ctx = new Context(LocaleContextHolder.getLocale());
            ctx.setVariable("otp", emailDTO.getBody());

            mailService.sendMimeMessage(emailDTO, ctx, "VerificationOtpTemplate");

            String hashOtp = passwordEncoder.encode(String.valueOf(otp));

            userEntity.getAuthIdentity().setPasswordResetCode(hashOtp);
            userRepository.save(userEntity);
            scheduleDeleteOtpExpired(username);

            // save token to redis
            redisTemplate.opsForValue().set(prefixOtp + username, hashOtp, 2, TimeUnit.MINUTES);
            // init count request validate otp
            redisTemplate.opsForValue().set(prefixOtpCountHit + username, 0, 2, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Send mail code OTP failed: {}", e.getMessage());
        }
    }

    @Override
    public TokenRefreshResponse validateOtp(String key, String otp) {
        String encodeOtp = getOPTByKey(prefixOtp + key);
        UserEntity userEntity = userRepository.findByUsername(key).orElseThrow(() ->
                new UserNotFoundException("Username with: " + key + " not found!"));
        if (encodeOtp == null) {
            encodeOtp = userEntity.getAuthIdentity().getPasswordResetCode();
        }

        if (!passwordEncoder.matches(otp, encodeOtp)) {
            throw new OTPException("Verify code OTP not extract!");
        }

        clearOTPFromCache(prefixOtp + key);
        // publish event clear otp inside database
        eventPublisher.publishEvent(new OnClearOtpResetPasswordEvent(this, userEntity.getUsername()));

        return generateTokenAfterVerifyOtp(userEntity);
    }

    public void scheduleDeleteOtpExpired(String username) {
        StopWatch stopWatch = StopWatch.createStarted();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new TimerTask() {
            @Override
            public void run() {
                stopWatch.stop();
                customUserRepository.findAndUpdatePasswordResetCode(username, null);
                executor.shutdown();
            }
        }, 3, TimeUnit.MINUTES);

    }

    /**
     * Generating a token after verify otp valid
     * @param userEntity - userEntity retrieve from database find by username
     * @return TokenRefreshResponse - response contains access token and refresh token*/
    private TokenRefreshResponse generateTokenAfterVerifyOtp(UserEntity userEntity) {
        try {
            Collection<? extends GrantedAuthority> grantedAuthorities = userEntity.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority(r.getRoleName().toString()))
                    .collect(Collectors.toList());

            User user =  new User(userEntity.getUsername(), userEntity.getPassword(), true, true,
                    true, true, grantedAuthorities);

            CompletableFuture<String>  jwt = this.jwtProvider.generateJwtToken(user, this.jwtExpirationMs);
            CompletableFuture.allOf(jwt).join();

            return new TokenRefreshResponse(jwt.get(), null);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Generate Token failed: {}", e.getMessage());
            throw new RuntimeException("Server generate token failed!");
        }
    }

    private Integer generateOtp() {
        return 100000 + new Random().nextInt(900000);
    }

    /**
     * Method for removing key from cache.
     * @param key - target key
     */
    private void clearOTPFromCache(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Method for getting OTP value by key.
     * @param key - target key
     * @return OTP value
     */
    private String getOPTByKey(String key)
    {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
