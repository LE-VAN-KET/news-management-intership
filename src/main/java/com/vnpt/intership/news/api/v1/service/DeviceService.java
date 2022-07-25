package com.vnpt.intership.news.api.v1.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.vnpt.intership.news.api.v1.domain.entity.DeviceMeta;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface DeviceService {
    DeviceMeta extractDevice(HttpServletRequest request);
}
