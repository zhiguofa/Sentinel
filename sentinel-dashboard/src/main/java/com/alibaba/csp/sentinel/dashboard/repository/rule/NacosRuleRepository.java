package com.alibaba.csp.sentinel.dashboard.repository.rule;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.domain.IDGenerator;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.common.utils.StringUtils;

public class NacosRuleRepository<E extends RuleEntity> extends NacosRepository<E, Long> {

    public NacosRuleRepository(String dataId, Class<E> cls,  ConfigService configService) {
        super(dataId, configService, cls, IDGenerator.getLongIDGenerator());
        this.init();
    }

    @Override
    protected Long findMaxId(Collection<E> rules) {
        return rules.stream().map(RuleEntity::getId).max(Long::compareTo).orElse(1L);
    }

    @Override
    public List<E> findAllByMachine(MachineInfo machineInfo) {
        Collection<E> rules = this.findAll();
        if(rules.isEmpty()) {
            return Collections.emptyList();
        }
        return rules.stream().filter(r -> {
            return StringUtils.equals(r.getApp(), machineInfo.getApp()) && 
                StringUtils.equals(r.getIp(), machineInfo.getIp()) &&
                machineInfo.getPort() != null & 
                machineInfo.getPort().equals(r.getPort());
        }).collect(Collectors.toList());
    }

}
