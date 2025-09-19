package com.example.domain.usecase;

import java.util.HashMap;
import java.util.Map;

public interface UseCase<I, O> {
    UResult<O> execute(I input);
}


