package com.musinsa.coordi.controller;

import com.musinsa.coordi.domain.Category;
import com.musinsa.coordi.domain.Coordi;
import com.musinsa.coordi.service.CoordiDto;
import com.musinsa.coordi.service.CoordiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoordiController.class)
class CoordiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CoordiService coordiService;

    @Test
    void getLowestEachCategories() throws Exception {
        // given
        Coordi coordi1 = Coordi.builder()
                .brand("A")
                .category(Category.TOP)
                .price(20_000)
                .build();
        Coordi coordi2 = Coordi.builder()
                .brand("B")
                .category(Category.PANTS)
                .price(10_000)
                .build();
        given(coordiService.getLowestEachCategories())
                .willReturn(List.of(coordi1, coordi2));

        // when
        ResultActions actions = mockMvc.perform(get("/coordis/lowest-category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].brand").value("A"))
                .andExpect(jsonPath("$.[0].price").value(20_000))
                .andExpect(jsonPath("$.[1].category").value("바지"))
                .andExpect(jsonPath("$.[1].price").value(10_000));
    }

    @Test
    void getLowestBrand() throws Exception {
        // given
        CoordiDto.CategoryPriceDto priceDto1 = new CoordiDto.CategoryPriceDto();
        priceDto1.setCategory("상의");
        priceDto1.setPrice("20,000");
        CoordiDto.CategoryPriceDto priceDto2 = new CoordiDto.CategoryPriceDto();
        priceDto2.setCategory("바지");
        priceDto2.setPrice("10,000");
        CoordiDto.LowestBrandDto.BrandDto brandDto = new CoordiDto.LowestBrandDto.BrandDto();
        brandDto.setBrand("A");
        brandDto.setCategories(List.of(priceDto1, priceDto2));
        brandDto.setTotalPrice("30,000");
        CoordiDto.LowestBrandDto dto = new CoordiDto.LowestBrandDto();
        dto.setLowest(brandDto);
        given(coordiService.getLowestBrand())
                .willReturn(dto);

        // when
        ResultActions actions = mockMvc.perform(get("/coordis/lowest-brand")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").value("A"))
                .andExpect(jsonPath("$.최저가.총액").value("30,000"))
                .andExpect(jsonPath("$.최저가.카테고리.[0].카테고리").value("상의"))
                .andExpect(jsonPath("$.최저가.카테고리.[0].가격").value("20,000"))
                .andExpect(jsonPath("$.최저가.카테고리.[1].카테고리").value("바지"))
                .andExpect(jsonPath("$.최저가.카테고리.[1].가격").value("10,000"));
    }

    @Test
    void getCategoryMinMax() throws Exception {
        // given
        String categoryName = "상의";
        CoordiDto.CategoryMinMaxDto.BrandPriceDto minBrandPrice = new CoordiDto.CategoryMinMaxDto.BrandPriceDto();
        minBrandPrice.setBrand("C");
        minBrandPrice.setPrice("10,000");
        CoordiDto.CategoryMinMaxDto.BrandPriceDto maxBrandPrice = new CoordiDto.CategoryMinMaxDto.BrandPriceDto();
        maxBrandPrice.setBrand("A");
        maxBrandPrice.setPrice("20,000");
        CoordiDto.CategoryMinMaxDto dto = new CoordiDto.CategoryMinMaxDto();
        dto.setCategory(categoryName);
        dto.setMin(List.of(minBrandPrice));
        dto.setMax(List.of(maxBrandPrice));
        given(coordiService.getCategoryMinmax(Category.of(categoryName)))
                .willReturn(dto);

        // when
        ResultActions actions = mockMvc.perform(get("/coordis/category-min-max")
                        .param("categoryName", categoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.카테고리").value(categoryName))
                .andExpect(jsonPath("$.최저가.[0].브랜드").value("C"))
                .andExpect(jsonPath("$.최저가.[0].가격").value("10,000"))
                .andExpect(jsonPath("$.최고가.[0].브랜드").value("A"))
                .andExpect(jsonPath("$.최고가.[0].가격").value("20,000"));
    }

    @Test
    void modifyCoordi() throws Exception {
        // given
        String request = """
                {
                  "mode": "수정",
                  "brand": "Z",
                  "prices": [
                    {"카테고리" : "상의", "가격" : "20,100"},
                    {"카테고리" : "액세서리", "가격" : "3,000"}
                  ]
                }
                """;
        String response = "브랜드 Z 수정 완료";
        given(coordiService.modifyCoordi(any()))
                .willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(post("/coordis")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("브랜드 Z 수정 완료"));

    }
}