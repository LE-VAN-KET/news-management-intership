package com.vnpt.intership.news.api.v1.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnpt.intership.news.api.v1.exception.ApiExceptionResponse;
import com.vnpt.intership.news.api.v1.exception.GlobalExceptionHandler;
import com.vnpt.intership.news.api.v1.exception.TokenException;
import com.vnpt.intership.news.api.v1.service.impl.UserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${com.app.token.prefix}")
    private String authTokenHeader;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    /*
    * Filter Per request for validate identity user
    * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String jwt = parseJwt(request);

            if (StringUtils.hasText(jwt) && this.jwtProvider.validateJwtToken(jwt)) {
                // retrieve username from jwt
                String username = this.jwtProvider.getUsernameFromJwtToken(jwt);

                // load user from database if exist, otherwise throw error
                UserDetails userDetails = this.userDetailService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails == null ? List.of() : userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (TokenException e) {
            ResponseEntity<?> error = globalExceptionHandler.handleExceptionUnauthorized(e);
            ApiExceptionResponse res = (ApiExceptionResponse) error.getBody();
            res.setTimestamp(null);
            response.setStatus(error.getStatusCodeValue());
            response.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter out = response.getWriter();
            out.print(mapper.writeValueAsString(res));
            out.flush();
            return;
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * parse token retrieve from header
     * slice prefix bearer token
     * */
    private String parseJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(authTokenHeader)) {
            return bearerToken.replace(authTokenHeader, "").trim();
        }
        return bearerToken;
    }
}
