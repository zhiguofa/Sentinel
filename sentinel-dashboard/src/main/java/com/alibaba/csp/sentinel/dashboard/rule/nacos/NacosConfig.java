/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import java.util.List;
import java.util.Properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Configuration
@EnableConfigurationProperties(SentinelNacosProperties.class)
@PropertySource(value = "classpath:nacos.properties")
public class NacosConfig {

    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }

    @Bean
    public ConfigService nacosConfigService(SentinelNacosProperties nacosProperties) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR,  nacosProperties.getServerAddr());
        properties.setProperty(PropertyKeyConst.NAMESPACE, nacosProperties.getNamespace());
        properties.setProperty(PropertyKeyConst.USERNAME, nacosProperties.getUsername());
        properties.setProperty(PropertyKeyConst.PASSWORD, nacosProperties.getPassword());
        properties.setProperty(PropertyKeyConst.ACCESS_KEY, nacosProperties.getAccessKey());
        properties.setProperty(PropertyKeyConst.SECRET_KEY, nacosProperties.getSecretKey());

        ConfigService configService = NacosFactory.createConfigService(properties);
        return configService;
    }
}
