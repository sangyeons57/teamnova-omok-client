package com.example.application.usecase;

import com.example.application.dto.response.RankingDataResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.core.exception.UseCaseException;

public class RankingDataUseCase extends UseCase<UseCase.None, RankingDataResponse> {

    private static final String ERROR_CODE = "RANKING_DATA_FAILED";

    private final UserRepository userRepository;

    public RankingDataUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository) {
        super(useCaseConfig);
        this.userRepository = userRepository;
    }

    @Override
    protected RankingDataResponse run(None input) throws UseCaseException {
        try {
            return new RankingDataResponse(userRepository.fetchRankingData());
        } catch (UseCaseException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw UseCaseException.of(ERROR_CODE, resolveMessage(exception, "Failed to load ranking data"));
        }
    }

    private String resolveMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return (message == null || message.trim().isEmpty()) ? fallback : message;
    }
}
