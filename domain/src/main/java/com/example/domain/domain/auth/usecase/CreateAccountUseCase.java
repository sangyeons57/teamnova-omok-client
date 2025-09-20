package com.example.domain.domain.auth.usecase;

import com.example.domain.domain.auth.model.GuestSignupResult;
import com.example.domain.domain.auth.model.LoginAction;
import com.example.domain.domain.auth.repository.LoginRepository;
import com.example.domain.application.SyncUseCase;
import com.example.domain.application.UseCaseException;

import java.util.Objects;

public class CreateAccountUseCase extends SyncUseCase<CreateAccountUseCase.Params, GuestSignupResult> {

    private final LoginRepository repository;

    public CreateAccountUseCase(LoginRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    protected GuestSignupResult run(Params params) throws UseCaseException {
        Objects.requireNonNull(params, "params");
        return repository.createAccount(params.getProvider(), params.getProviderUserId());
    }

    public static final class Params {
        private final LoginAction provider;
        private final String providerUserId;

        private Params(LoginAction provider, String providerUserId) {
            this.provider = Objects.requireNonNull(provider, "provider");
            this.providerUserId = providerUserId;
        }

        public static Params forGuest() {
            return new Params(LoginAction.GUEST, null);
        }

        public static Params forGoogle(String providerUserId) {
            return new Params(LoginAction.GOOGLE, Objects.requireNonNull(providerUserId, "providerUserId"));
        }

        public LoginAction getProvider() {
            return provider;
        }

        public String getProviderUserId() {
            return providerUserId;
        }
    }
}
