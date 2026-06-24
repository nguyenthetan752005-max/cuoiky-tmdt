package com.goodminton.controller;

import com.goodminton.entity.SystemConfig;
import com.goodminton.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SystemConfigService systemConfigService;

    @ModelAttribute("globalConfig")
    public SystemConfig addGlobalConfigToModel() {
        // Biến 'globalConfig' sẽ có sẵn ở TOÀN BỘ các file Thymeleaf (.html)
        return systemConfigService.getConfig();
    }
}
