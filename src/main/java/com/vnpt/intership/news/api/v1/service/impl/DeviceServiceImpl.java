package com.vnpt.intership.news.api.v1.service.impl;

import com.google.common.base.Strings;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;
import com.vnpt.intership.news.api.v1.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

@Slf4j
@Component
public class DeviceServiceImpl implements DeviceService {
    private Parser parser;

    private DatabaseReader databaseReader;

    public DeviceServiceImpl(@Qualifier("GeoIPCity") DatabaseReader databaseReader, Parser parser) {
        this.parser = parser;
        this.databaseReader = databaseReader;
    }

    @Override
    public DeviceMeta extractDevice(HttpServletRequest request){
        try {
            String ip = extractIp(request);
            String location = getIpLocation(ip);
            String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));
            DeviceMeta deviceMeta = new DeviceMeta();
            deviceMeta.setLocation(location);
            deviceMeta.setDeviceDetails(deviceDetails);
            return deviceMeta;
        } catch (IOException | GeoIp2Exception e ) {
            log.error("Extract Info Device ERROR: {}", e.getMessage());
            throw new RuntimeException("Extract Info Device ERROR");
        }
    }

    private String extractIp(HttpServletRequest request) {
        String clientIp;
        String clientXForwardedForIp = request.getHeader("x-forwarded-for");
        if (Objects.nonNull(clientXForwardedForIp)) {
            clientIp = parseXForwardedHeader(clientXForwardedForIp);
        } else {
            clientIp = request.getRemoteAddr();
        }

        if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
            clientIp = "20.205.243.166"; // test localhost
        }

        return clientIp;
    }

    private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }

    private String getDeviceDetails(String userAgent) {
        String deviceDetails = "UNKNOWN";

        Client client = parser.parse(userAgent);
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
                    " - " + client.os.family + " " + client.os.major + "." + client.os.minor;
        }

        return deviceDetails;
    }

    private String getIpLocation(String ip) throws IOException, GeoIp2Exception {

        String location = "UNKNOWN";

        InetAddress ipAddress = InetAddress.getByName(ip);

        CityResponse cityResponse = databaseReader.city(ipAddress);
        if (Objects.nonNull(cityResponse) && Objects.nonNull(cityResponse.getCity()) &&
                !Strings.isNullOrEmpty(cityResponse.getCity().getName())) {
            location = cityResponse.getCity().getName();
        }

        return location;
    }

}
