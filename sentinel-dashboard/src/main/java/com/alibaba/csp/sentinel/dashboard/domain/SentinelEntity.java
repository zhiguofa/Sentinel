package com.alibaba.csp.sentinel.dashboard.domain;

public interface SentinelEntity<ID> {
    ID getId();

    void setId(ID id);
}
