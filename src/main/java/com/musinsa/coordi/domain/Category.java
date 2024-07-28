package com.musinsa.coordi.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {

    TOP("상의"),
    OUTER("아우터"),
    PANTS("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("액세서리")
    ;

    @JsonValue
    private final String value;

    Category(String value) {
        this.value= value;
    }

    public static Category of(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        return Arrays.stream(Category.values())
                .filter(category -> category.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid category name: " + value));
    }
}
