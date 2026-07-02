package com.alibaba.csp.sentinel.dashboard.domain;

import java.util.concurrent.atomic.AtomicLong;

public interface IDGenerator<T> {

    T next();

    void update(T idVal);

    public static LongIDGenerator getLongIDGenerator() {
        return new LongIDGenerator(0L);
    }

    public static class LongIDGenerator implements IDGenerator<Long> {

        private AtomicLong generator;

        public LongIDGenerator(Long currentValue) {
            generator = new AtomicLong(currentValue);
        }

        @Override
        public Long next() {
            return generator.incrementAndGet();
        }

        @Override
        public void update(Long idVal) {
            this.generator.set(idVal);
        }
    }
}

