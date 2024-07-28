package com.musinsa.coordi.service;

import com.musinsa.coordi.domain.Category;
import com.musinsa.coordi.domain.Coordi;
import com.musinsa.coordi.domain.CoordiRepository;
import com.musinsa.coordi.domain.LowestBrand;
import com.musinsa.coordi.exception.InvalidDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CoordiServiceTest {

    @InjectMocks
    CoordiService coordiService;

    @Mock
    CoordiRepository coordiRepository;

    @Test
    void getLowestEachCategories() {
        // given
        Category categoryTop = Category.TOP;
        Coordi coordiTop = Coordi.builder()
                .brand("A")
                .category(categoryTop)
                .price(10_000)
                .build();
        Category categoryOuter = Category.OUTER;
        Coordi coordiOuter = Coordi.builder()
                .brand("B")
                .category(categoryOuter)
                .price(20_000)
                .build();
        Category categoryPants = Category.PANTS;
        Coordi coordiPants = Coordi.builder()
                .brand("C")
                .category(categoryPants)
                .price(5_000)
                .build();
        Category categorySneakers = Category.SNEAKERS;
        Coordi coordiSneakers = Coordi.builder()
                .brand("D")
                .category(categorySneakers)
                .price(5_000)
                .build();
        Category categoryBag = Category.BAG;
        Coordi coordiBag = Coordi.builder()
                .brand("E")
                .category(categoryBag)
                .price(5_000)
                .build();
        Category categoryHat = Category.HAT;
        Coordi coordiHat = Coordi.builder()
                .brand("F")
                .category(categoryHat)
                .price(5_000)
                .build();
        Category categorySocks = Category.SOCKS;
        Coordi coordiSocks = Coordi.builder()
                .brand("G")
                .category(categorySocks)
                .price(5_000)
                .build();
        Category categoryAccessory = Category.ACCESSORY;
        Coordi coordiAccessory = Coordi.builder()
                .brand("H")
                .category(categoryAccessory)
                .price(5_000)
                .build();
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categoryTop))
                .willReturn(Optional.of(coordiTop));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categoryOuter))
                .willReturn(Optional.of(coordiOuter));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categoryPants))
                .willReturn(Optional.of(coordiPants));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categorySneakers))
                .willReturn(Optional.of(coordiSneakers));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categoryBag))
                .willReturn(Optional.of(coordiBag));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categoryHat))
                .willReturn(Optional.of(coordiHat));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categorySocks))
                .willReturn(Optional.of(coordiSocks));
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(categoryAccessory))
                .willReturn(Optional.of(coordiAccessory));

        // when
        CoordiDto.LowestCategoryDto dto = coordiService.getLowestEachCategories();

        // then
        assertThat(dto.getTotalPrice()).isEqualTo(60_000);
        assertThat(dto.getProducts().size()).isEqualTo(8);
        Coordi coordi = dto.getProducts().get(6);
        assertThat(coordi.getBrand()).isEqualTo("G");
    }

    @Test
    void getLowestEachCategories_error() {
        // given
        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(Category.TOP))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(InvalidDataException.class, () -> coordiService.getLowestEachCategories());
    }

    @Test
    void getLowestBrand() {
        // given
        String brand = "A";
        LowestBrand lowestBrand = new LowestBrand(brand, 60_000L);
        Category categoryTop = Category.TOP;
        Coordi coordiTop = Coordi.builder()
                .brand(brand)
                .category(categoryTop)
                .price(10_000)
                .build();
        Category categoryOuter = Category.OUTER;
        Coordi coordiOuter = Coordi.builder()
                .brand(brand)
                .category(categoryOuter)
                .price(20_000)
                .build();
        Category categoryPants = Category.PANTS;
        Coordi coordiPants = Coordi.builder()
                .brand(brand)
                .category(categoryPants)
                .price(5_000)
                .build();
        Category categorySneakers = Category.SNEAKERS;
        Coordi coordiSneakers = Coordi.builder()
                .brand(brand)
                .category(categorySneakers)
                .price(5_000)
                .build();
        Category categoryBag = Category.BAG;
        Coordi coordiBag = Coordi.builder()
                .brand(brand)
                .category(categoryBag)
                .price(5_000)
                .build();
        Category categoryHat = Category.HAT;
        Coordi coordiHat = Coordi.builder()
                .brand(brand)
                .category(categoryHat)
                .price(5_000)
                .build();
        Category categorySocks = Category.SOCKS;
        Coordi coordiSocks = Coordi.builder()
                .brand(brand)
                .category(categorySocks)
                .price(5_000)
                .build();
        Category categoryAccessory = Category.ACCESSORY;
        Coordi coordiAccessory = Coordi.builder()
                .brand(brand)
                .category(categoryAccessory)
                .price(5_000)
                .build();

        given(coordiRepository.findLowestBrand())
                .willReturn(lowestBrand);
        given(coordiRepository.findAllByBrand(brand))
                .willReturn(List.of(coordiTop, coordiOuter, coordiPants, coordiSneakers, coordiBag, coordiHat,
                        coordiSocks, coordiAccessory));

        // when
        CoordiDto.LowestBrandDto dto = coordiService.getLowestBrand();

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getLowest().getBrand()).isEqualTo(brand);
        assertThat(dto.getLowest().getCategories().size()).isEqualTo(8);
    }

    @Test
    void getLowestBrand_error() {
        // given
        String brand = "A";
        LowestBrand lowestBrand = new LowestBrand(brand, 60_000L);
        given(coordiRepository.findLowestBrand())
                .willReturn(lowestBrand);
        given(coordiRepository.findAllByBrand(brand))
                .willReturn(List.of());

        // when & then
        assertThrows(InvalidDataException.class, () -> coordiService.getLowestBrand());
    }

    @Test
    void getCategoryMinmax() {
        // given
        Category category = Category.TOP;
        Coordi coordiTopMin = Coordi.builder()
                .brand("A")
                .category(category)
                .price(10_000)
                .build();
        Coordi coordiTopMax= Coordi.builder()
                .brand("F")
                .category(category)
                .price(30_000)
                .build();

        given(coordiRepository.findFirstByCategoryOrderByPriceAsc(category))
                .willReturn(Optional.of(coordiTopMin));
        given(coordiRepository.findFirstByCategoryOrderByPriceDesc(category))
                .willReturn(Optional.of(coordiTopMax));
        given(coordiRepository.findAllByCategoryAndPrice(category, coordiTopMin.getPrice()))
                .willReturn(List.of(coordiTopMin));
        given(coordiRepository.findAllByCategoryAndPrice(category, coordiTopMax.getPrice()))
                .willReturn(List.of(coordiTopMax));

        // when
        CoordiDto.CategoryMinMaxDto dto = coordiService.getCategoryMinmax(category);

        // then
        assertThat(dto.getMin().get(0).getBrand()).isEqualTo("A");
        assertThat(dto.getMin().get(0).getPrice()).isEqualTo("10,000");
        assertThat(dto.getMax().get(0).getBrand()).isEqualTo("F");
        assertThat(dto.getMax().get(0).getPrice()).isEqualTo("30,000");
    }

    @Test
    void modifyCoordi_error() {
        // given
        CoordiDto.RequestDto request = new CoordiDto.RequestDto();
        request.setMode("기타");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> coordiService.modifyCoordi(request));
    }
}