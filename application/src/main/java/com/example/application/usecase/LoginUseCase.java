package com.example.application.usecase;

import android.util.Log;

import com.example.application.dto.response.LoginResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.application.session.UserSessionStore;
import com.example.application.wrapper.UserSession;
import com.example.core_api.exception.UseCaseException;

/**
 *  엑세스 토큰을 제공해서 자동으로 로그인하고
 * 로그인 성공 여부 확인하고
 * 그 결과 돌려주는 UseCase
 * 엑세스 토큰 실패는 다른단계에서 리프래시 토큰 제공해야함
 */
public class LoginUseCase extends UseCase<UseCase.None, LoginResponse> {
    private final IdentifyRepository identifyRepository;
    private final UserSessionStore userSessionStore;

    public LoginUseCase(UseCaseConfig config,
                        IdentifyRepository identifyRepository,
                        UserSessionStore userSessionStore) {
        super(config);
        this.identifyRepository = identifyRepository;
        this.userSessionStore = userSessionStore;
    }

    @Override
    protected LoginResponse run(None input) throws UseCaseException {
        UserSession identity = identifyRepository.login();
        userSessionStore.update(identity);
        Log.d("LoginUseCase", "identity: " + identity.getUser().toString());

        return new LoginResponse(identity.getUser());
    }
}
