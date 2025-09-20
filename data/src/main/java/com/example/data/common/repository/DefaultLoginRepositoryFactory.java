package com.example.data.common.repository;

import com.example.core.network.http.HttpClient;
import com.example.data.common.datasource.DefaultPhpServerDataSource;
import com.example.data.common.mapper.GuestSignupMapper;
import com.example.domain.domain.auth.repository.LoginRepository;

public final class DefaultLoginRepositoryFactory {
    private final DefaultPhpServerDataSource phpServerDataSource;
    private final HelloWorldMapper helloWorldMapper;
    private final GuestSignupMapper guestSignupMapper;
    private volatile LoginRepository singleton;

    public DefaultLoginRepositoryFactory(HttpClient httpClient) {
        phpServerDataSource = new DefaultPhpServerDataSource(httpClient);
        helloWorldMapper = new HelloWorldMapper();
        guestSignupMapper = new GuestSignupMapper();
    }

    public LoginRepository create() {
        if (singleton != null) {
            return singleton;
        }
        synchronized (this) {
            if (singleton == null) {
                singleton = new DefaultLoginRepository(
                        phpServerDataSource,
                        helloWorldMapper,
                        guestSignupMapper
                );
            }
            return singleton;
        }
    }
}
