package com.example.data.repository.factory;

import com.example.application.port.out.IdentifyRepository;
import com.example.core.network.http.HttpClient;
import com.example.data.datasource.DefaultPhpServerDataSource;
import com.example.data.mapper.IdentityMapper;
import com.example.data.repository.IdentifyRepositoryImpl;

public final class IdentifyRepositoryFactory {
    private final DefaultPhpServerDataSource phpServerDataSource;
    private final IdentityMapper identityMapper;
    private volatile IdentifyRepository singleton;

    public IdentifyRepositoryFactory(HttpClient httpClient) {
        phpServerDataSource = new DefaultPhpServerDataSource(httpClient);
        identityMapper = new IdentityMapper();
    }

    public IdentifyRepository create() {
        if (singleton != null) {
            return singleton;
        }
        synchronized (this) {
            if (singleton == null) {
                singleton = new IdentifyRepositoryImpl(
                        phpServerDataSource,
                        identityMapper
                );
            }
            return singleton;
        }
    }
}
