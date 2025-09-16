package com.example.data.auth.repository;

import com.example.core.network.http.HttpClientManager;
import com.example.data.auth.datasource.HelloWorldRemoteDataSource;
import com.example.data.auth.mapper.HelloWorldMapper;
import com.example.domain.auth.model.HelloWorldMessage;
import com.example.domain.auth.model.LoginAction;
import com.example.domain.auth.repository.LoginRepository;

import java.util.Objects;

/**
 * Default implementation of the login repository which orchestrates
 * login related actions and fetches data from remote sources when needed.
 */
public class DefaultLoginRepository implements LoginRepository {

    private final HelloWorldRemoteDataSource remoteDataSource;
    private final HelloWorldMapper mapper;

    public DefaultLoginRepository() {
        this(new HelloWorldRemoteDataSource(HttpClientManager.getInstance()), new HelloWorldMapper());
    }

    public DefaultLoginRepository(HelloWorldRemoteDataSource remoteDataSource, HelloWorldMapper mapper) {
        this.remoteDataSource = Objects.requireNonNull(remoteDataSource, "remoteDataSource");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public LoginAction loginAsGuest() {
        return LoginAction.GUEST;
    }

    @Override
    public LoginAction loginWithGoogle() {
        return LoginAction.GOOGLE;
    }

    @Override
    public HelloWorldMessage getHelloWorldMessage() {
        return mapper.toDomain(remoteDataSource.getHelloWorld());
    }
}
