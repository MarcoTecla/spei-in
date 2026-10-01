package com.mx.spei.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FineractProperties.class, SpeiProperties.class})
public class PropertiesConfig { }
