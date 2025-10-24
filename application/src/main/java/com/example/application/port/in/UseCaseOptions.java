package com.example.application.port.in;

import com.example.core_api.retry.RetryPolicy;

import java.util.concurrent.ScheduledExecutorService;

public class UseCaseOptions {
    private final Option<Boolean> retryEnabled;
    private final Option<RetryPolicy> retryPolicy;
    private final Option<ScheduledExecutorService> scheduler;

    private UseCaseOptions(Builder builder) {
        this.retryEnabled = builder.retryEnabled;
        this.retryPolicy = builder.retryPolicy;
        this.scheduler = builder.scheduler;
    }

    public boolean getRetryEnabled(boolean defaultValue) {
        return retryEnabled.isAssigned ? retryEnabled.value : defaultValue;
    }
    public RetryPolicy getRetryPolicy(RetryPolicy defaultValue) {
        return retryPolicy.isAssigned ? retryPolicy.value : defaultValue;
    }
    public ScheduledExecutorService getScheduler(ScheduledExecutorService defaultValue) {
        return scheduler.isAssigned ? scheduler.value : defaultValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Option<T> {
        private boolean isAssigned;
        private T value;
        private Option() {
            isAssigned = false;
        }
        private void set(T value) {
            isAssigned = true;
            this.value = value;
        }
        public T get() {
            return value;
        }
    }

    public static class Builder {
        private final Option<Boolean> retryEnabled = new Option<>();
        private final Option<RetryPolicy> retryPolicy = new Option<>();
        private final Option<ScheduledExecutorService> scheduler = new Option<>();

        public Builder retryEnabled(Boolean retryEnabled) {
            this.retryEnabled.set(retryEnabled);
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy.set(retryPolicy);
            return this;
        }

        public Builder scheduler(ScheduledExecutorService scheduler) {
            this.scheduler.set(scheduler);
            return this;
        }

        public UseCaseOptions build() {
            return new UseCaseOptions(this);
        }
    }
}
