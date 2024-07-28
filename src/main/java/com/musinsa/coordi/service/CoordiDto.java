package com.musinsa.coordi.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CoordiDto {

    @Getter
    @Setter
    public static class LowestBrandDto {
        @JsonProperty(value = "최저가")
        private BrandDto lowest;

        @Getter
        @Setter
        public static class BrandDto {
            @JsonProperty(value = "브랜드")
            private String brand;
            @JsonProperty(value = "카테고리")
            private List<CategoryPriceDto> categories;
            @JsonProperty(value = "총액")
            private String totalPrice;

        }
    }

    @Getter
    @Setter
    public static class CategoryPriceDto {
        @JsonProperty(value = "카테고리")
        private String category;
        @JsonProperty(value = "가격")
        private String price;
    }

    @Getter
    @Setter
    public static class CategoryMinMaxDto {
        @JsonProperty(value = "카테고리")
        private String category;
        @JsonProperty(value = "최저가")
        private List<BrandPriceDto> min;
        @JsonProperty(value = "최고가")
        private List<BrandPriceDto> max;

        @Getter
        @Setter
        public static class BrandPriceDto {
            @JsonProperty(value = "브랜드")
            private String brand;
            @JsonProperty(value = "가격")
            private String price;
        }
    }

    @Getter
    @Setter
    public static class RequestDto {
        @NotEmpty
        private String mode;
        @NotEmpty
        private String brand;

        private List<CategoryPriceDto> prices;
    }
}
