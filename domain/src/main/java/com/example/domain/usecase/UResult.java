package com.example.domain.usecase;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Describes a synchronous unit of domain work.
 */

// 해당 result는 Usecae 레이어 에서만 사용하는 Result 입니다.
public sealed interface UResult<T> {
    record Ok<T>(T value) implements UResult<T> {}
    record Err<T>(String code, String message) implements UResult<T> {}
}

