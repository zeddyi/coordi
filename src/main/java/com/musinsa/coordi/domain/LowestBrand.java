package com.musinsa.coordi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LowestBrand {
    private String brand;
    private Integer price;

    public LowestBrand(String brand, Long price) {
        this.brand = brand;
        this.price = price.intValue();
    }
}
