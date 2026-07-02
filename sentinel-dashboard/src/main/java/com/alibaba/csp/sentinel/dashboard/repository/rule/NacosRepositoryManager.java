package com.alibaba.csp.sentinel.dashboard.repository.rule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.nacos.api.config.ConfigService;

public class NacosRepositoryManager<E extends RuleEntity> {

    private Map<String, NacosRuleRepository<E>> repositories = new ConcurrentHashMap<>();

    private ConfigService configService;

    private String suffix;

    private Class<E> cls;

    public NacosRepositoryManager(ConfigService configService, String suffix, Class<E> cls) {
        this.configService = configService;
        this.suffix = suffix;
        this.cls = cls;
    }

    public RuleRepository<E, Long> getRuleRepository(String app) {
        return repositories.computeIfAbsent(app, t -> new NacosRuleRepository<>(t + suffix, cls, configService));
    }
}
