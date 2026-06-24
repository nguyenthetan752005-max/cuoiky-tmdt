package com.goodminton.service;

import com.goodminton.entity.SystemConfig;
import com.goodminton.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public SystemConfig getConfig() {
        return systemConfigRepository.findById(1L).orElseGet(() -> {
            SystemConfig defaultConfig = new SystemConfig();
            return systemConfigRepository.save(defaultConfig);
        });
    }

    public SystemConfig updateConfig(SystemConfig updatedConfig) {
        SystemConfig config = getConfig();
        config.setBaseShippingFee(updatedConfig.getBaseShippingFee());
        config.setFreeshipThreshold(updatedConfig.getFreeshipThreshold());
        config.setStoreName(updatedConfig.getStoreName());
        config.setHotline(updatedConfig.getHotline());
        config.setContactEmail(updatedConfig.getContactEmail());
        config.setStoreAddress(updatedConfig.getStoreAddress());
        config.setGlobalAnnouncement(updatedConfig.getGlobalAnnouncement());
        
        return systemConfigRepository.save(config);
    }
}
