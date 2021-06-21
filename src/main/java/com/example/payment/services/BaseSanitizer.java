package com.example.payment.services;


import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface BaseSanitizer<T> {

    default String encode(String toEncode) {
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    @SneakyThrows
    default String decode(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode.getBytes()), StandardCharsets.UTF_8);
    }

    T sanitizeEntity(T entity);

    T deSanitizeEntity(T entity);
}
