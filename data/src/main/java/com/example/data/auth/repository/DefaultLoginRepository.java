package com.example.data.auth.repository;

import com.example.core.network.http.HttpClientManager;
import com.example.data.auth.datasource.HelloWorldRemoteDataSource;
import com.example.data.auth.datasource.remote.AuthRemoteDataSource;
import com.example.data.auth.mapper.GuestSignupMapper;
import com.example.data.auth.mapper.HelloWorldMapper;
import com.example.data.auth.model.GuestSignupResponse;
import com.example.domain.auth.model.GuestSignupResult;
import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.repository.LoginRepository;

import java.util.Objects;

/**
 * Default implementation of the login repository which orchestrates
 * login related actions and fetches data from remote sources when needed.
 */
public class DefaultLoginRepository implements LoginRepository {

    private final HelloWorldRemoteDataSource helloWorldRemoteDataSource;
    private final HelloWorldMapper helloWorldMapper;
    private final AuthRemoteDataSource authRemoteDataSource;
    private final GuestSignupMapper guestSignupMapper;

    public DefaultLoginRepository() {
        this(
                new HelloWorldRemoteDataSource(HttpClientManager.getInstance()),
                new HelloWorldMapper(),
                new AuthRemoteDataSource(HttpClientManager.getInstance()),
                new GuestSignupMapper()
        );
    }

    public DefaultLoginRepository(HelloWorldRemoteDataSource remoteDataSource,
                                   HelloWorldMapper mapper,
                                   AuthRemoteDataSource authRemoteDataSource,
                                   GuestSignupMapper guestSignupMapper) {
        this.helloWorldRemoteDataSource = Objects.requireNonNull(remoteDataSource, "remoteDataSource");
        this.helloWorldMapper = Objects.requireNonNull(mapper, "mapper");
        this.authRemoteDataSource = Objects.requireNonNull(authRemoteDataSource, "authRemoteDataSource");
        this.guestSignupMapper = Objects.requireNonNull(guestSignupMapper, "guestSignupMapper");
    }

    @Override
    public GuestSignupResult createAccount(LoginAction provider, String providerUserId) {
        GuestSignupResponse response = authRemoteDataSource.createAccount(provider.name(), providerUserId);
        return guestSignupMapper.toDomain(response);
    }

    @Override
    public HelloWorldMessage getHelloWorldMessage() {
        return helloWorldMapper.toDomain(helloWorldRemoteDataSource.getHelloWorld());
    }
}
