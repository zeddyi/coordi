package com.musinsa.coordi.controller;

import com.musinsa.coordi.domain.Category;
import com.musinsa.coordi.service.CoordiDto;
import com.musinsa.coordi.service.CoordiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CoordiController {

    private final CoordiService coordiService;

    @GetMapping("/coordis/lowest-category")
    public CoordiDto.LowestCategoryDto getLowestEachCategories() {
        return coordiService.getLowestEachCategories();
    }

    @GetMapping("/coordis/lowest-brand")
    public CoordiDto.LowestBrandDto getLowestBrand() {
        return coordiService.getLowestBrand();
    }

    @GetMapping("/coordis/category-min-max")
    public CoordiDto.CategoryMinMaxDto getCategoryMinMax(@RequestParam String categoryName) {
        Category category = Category.of(categoryName);

        return coordiService.getCategoryMinmax(category);
    }

    @PostMapping("/coordis")
    public Response modifyCoordi(@RequestBody @Valid CoordiDto.RequestDto request) {
        String result = coordiService.modifyCoordi(request);
        return new Response(true, HttpStatus.OK.name(), result);
    }

}
