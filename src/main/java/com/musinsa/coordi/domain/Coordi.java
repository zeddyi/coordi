package com.musinsa.coordi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Coordi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String brand;
    @Enumerated(EnumType.STRING)
    private Category category;
    private Integer price;

    @Builder
    public Coordi(String brand, Category category, Integer price) {
        this.brand = brand;
        this.category = category;
        this.price = price;
    }
}
