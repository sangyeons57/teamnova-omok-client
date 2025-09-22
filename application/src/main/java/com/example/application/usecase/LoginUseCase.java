package com.example.application.usecase;

import com.example.application.dto.command.LoginCommand;
import com.example.application.dto.response.LoginResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.core.exception.UseCaseException;
import com.example.core.token.TokenManager;

import kotlin.coroutines.ContinuationInterceptor;

/**
 *  엑세스 토큰을 제공해서 자동으로 로그인하고
 * 로그인 성공 여부 확인하고
 * 그 결과 돌려주는 UseCase
 *
 * 엑세스 토큰 실패는 다른단계에서 리프래시 토큰 제공해야함
 */
public class LoginUseCase extends UseCase<UseCase.None, LoginResponse> {
    public LoginUseCase(UseCaseConfig config) {
        super(config);
    }

    @Override
    protected LoginResponse run(None input) throws UseCaseException {
        return null;
    }
}
