package com.example.payment.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldValidator {

    public static <T> List<String> checkNull(T testedClass, Class<T> classType) {
        if (isNull(testedClass)) {
            return Stream.of(classType.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        }
        return Stream.of(testedClass.getClass().getDeclaredFields())
                .filter(field -> isFieldValueNull(testedClass, field))
                .map(Field::getName)
                .collect(Collectors.toList());
    }

    private static <T> boolean isFieldValueNull(T testedClass, Field field) {
        try {
            field.setAccessible(true);
            return field.get(testedClass) == null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
