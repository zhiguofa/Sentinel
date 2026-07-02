package com.alibaba.csp.sentinel.dashboard.repository.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.alibaba.csp.sentinel.dashboard.domain.IDGenerator;
import com.alibaba.csp.sentinel.dashboard.domain.SentinelEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;

public abstract class NacosRepository<E extends SentinelEntity<ID>, ID> implements RuleRepository<E, ID> {

    private ConcurrentHashMap<ID, E> ruleCache = new ConcurrentHashMap<>();

    private ConfigService configService;

    private String dataId;

    private Class<E> cls;

    private IDGenerator<ID> idGenerator;

    private boolean loaded;

    public NacosRepository(String dataId, ConfigService configService, Class<E> cls, IDGenerator<ID> generator) {
        this.idGenerator = generator;
        this.configService = configService;
        this.dataId = dataId;
        this.cls = cls;
    }

    public void init() {
        Collection<E> rules = this.findAll();
        if(rules != null && !rules.isEmpty()) {
            ID idVal = findMaxId(rules);
            idGenerator.update(idVal);
        }
    }

    public ID nextId() {
        return idGenerator.next();
    }

    protected abstract ID findMaxId(Collection<E> rules);

    protected String toJson(Collection<E> rules) {
        try {
            return JSON.toJSONString(rules);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public E save(E entity) {
        if (entity.getId() == null) {
            entity.setId(nextId());
        }
        ruleCache.put(entity.getId(), entity);
        this.publishConfig(ruleCache.values());
        return entity;
    }

    @Override
    public E delete(ID id) {
        E answer = ruleCache.remove(id);
        this.publishConfig(ruleCache.values());
        return answer;
    }

    @Override
    public E findById(ID id) {
        return ruleCache.get(id);
    }

    protected Collection<E> findAll() {
        if(loaded) {
            return ruleCache.values();
        }
        Collection<E> rules = loadConfig();
        if (!CollectionUtils.isEmpty(rules)) {
            rules.forEach(r -> ruleCache.put(r.getId(), r));
        }
        loaded = true;
        return ruleCache.values();
    }

    @Override
    public List<E> saveAll(List<E> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }
        ruleCache.clear();
        rules.forEach(r -> ruleCache.put(r.getId(), r));
        this.publishConfig(rules);
        return rules;
    }

    @Override
    public List<E> findAllByApp(String appName) {
        Collection<E> rules = this.findAll();
        if(rules.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(rules);
    }

    public void publishConfig(Collection<E> rules) {
        try {
            configService.publishConfig(dataId, NacosConfigUtil.GROUP_ID,
                    toJson(rules),
                    ConfigType.JSON.getType());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Collection<E> loadConfig() {
        try {
            String rules = configService.getConfig(dataId, NacosConfigUtil.GROUP_ID, 3000);
            if (rules == null) {
                return Collections.emptyList();
            }
            return JSON.parseArray(rules, cls);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
