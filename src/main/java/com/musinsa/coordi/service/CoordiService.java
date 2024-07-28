package com.musinsa.coordi.service;

import com.musinsa.coordi.domain.Category;
import com.musinsa.coordi.domain.Coordi;
import com.musinsa.coordi.domain.CoordiRepository;
import com.musinsa.coordi.domain.LowestBrand;
import com.musinsa.coordi.exception.AlreadyExistException;
import com.musinsa.coordi.exception.InvalidDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoordiService {

    private final CoordiRepository coordiRepository;

    public List<Coordi> getLowestEachCategories() {
        List<Coordi> coordis = new ArrayList<>();

        for (Category category : Category.values()) {
            Optional<Coordi> coordi = coordiRepository.findFirstByCategoryOrderByPriceAsc(category);
            if (coordi.isEmpty()) {
                throw new InvalidDataException("카테고리 최저가 조회 실패: " + category.getValue());
            }
            coordis.add(coordi.get());
        }

        return coordis;
    }

    public CoordiDto.LowestBrandDto getLowestBrand() {
        LowestBrand lowestBrand = coordiRepository.findLowestBrand();

        List<Coordi> coordis = coordiRepository.findAllByBrand(lowestBrand.getBrand());

        if (CollectionUtils.isEmpty(coordis)) {
            throw new InvalidDataException("최저가 브랜드 조회 실패");
        }

        CoordiDto.LowestBrandDto.BrandDto brand = new CoordiDto.LowestBrandDto.BrandDto();
        brand.setBrand(lowestBrand.getBrand());
        brand.setCategories(convertCategoryPrices(coordis));
        brand.setTotalPrice(String.format("%,d", lowestBrand.getPrice()));

        CoordiDto.LowestBrandDto lowest = new CoordiDto.LowestBrandDto();
        lowest.setLowest(brand);

        return lowest;
    }

    private List<CoordiDto.CategoryPriceDto> convertCategoryPrices(
            List<Coordi> coordis) {
        List<CoordiDto.CategoryPriceDto> prices = new ArrayList<>();
        for (Coordi coordi : coordis) {
            CoordiDto.CategoryPriceDto categoryPriceDto = new CoordiDto.CategoryPriceDto();
            categoryPriceDto.setCategory(coordi.getCategory().getValue());
            categoryPriceDto.setPrice(String.format("%,d", coordi.getPrice()));

            prices.add(categoryPriceDto);
        }

        return prices;
    }

    public CoordiDto.CategoryMinMaxDto getCategoryMinmax(Category category) {
        Optional<Coordi> minPriceCoordi = coordiRepository.findFirstByCategoryOrderByPriceAsc(category);
        Optional<Coordi> maxPriceCoordi = coordiRepository.findFirstByCategoryOrderByPriceDesc(category);

        if (minPriceCoordi.isEmpty() || maxPriceCoordi.isEmpty()) {
            throw new InvalidDataException("카테고리 최저, 최고 가격 조회 실패: " + category.getValue());
        }

        List<Coordi> minList = coordiRepository.findAllByCategoryAndPrice(category, minPriceCoordi.get().getPrice());
        List<Coordi> maxList = coordiRepository.findAllByCategoryAndPrice(category, maxPriceCoordi.get().getPrice());

        CoordiDto.CategoryMinMaxDto dto = new CoordiDto.CategoryMinMaxDto();
        dto.setCategory(category.getValue());
        dto.setMin(convertBrandPrices(minList));
        dto.setMax(convertBrandPrices(maxList));

        return dto;
    }

    private List<CoordiDto.CategoryMinMaxDto.BrandPriceDto> convertBrandPrices(List<Coordi> coordis) {
        List<CoordiDto.CategoryMinMaxDto.BrandPriceDto> prices = new ArrayList<>();

        for (Coordi coordi : coordis) {
            CoordiDto.CategoryMinMaxDto.BrandPriceDto brandPriceDto = new CoordiDto.CategoryMinMaxDto.BrandPriceDto();
            brandPriceDto.setBrand(coordi.getBrand());
            brandPriceDto.setPrice(String.format("%,d", coordi.getPrice()));

            prices.add(brandPriceDto);
        }

        return prices;
    }

    @Transactional
    public String modifyCoordi(CoordiDto.RequestDto request) {
        return switch (request.getMode()) {
            case "추가" -> {
                add(request);
                yield "브랜드 " + request.getBrand() + " 추가 완료";
            }
            case "수정" -> {
                update(request);
                yield "브랜드 " + request.getBrand() + " 수정 완료";
            }
            case "삭제" -> {
                delete(request);
                yield "브랜드 " + request.getBrand() + " 삭제 완료";
            }
            default -> throw new IllegalArgumentException("잘못된 모드: " + request.getMode());
        };
    }

    private void add(CoordiDto.RequestDto request) {
        if (CollectionUtils.isEmpty(request.getPrices()) || request.getPrices().size() != 8) {
            throw new InvalidDataException("카테고리 부족: " + request.getPrices().size());
        }

        boolean exist = coordiRepository.existsByBrand(request.getBrand());

        if (exist) {
            throw new AlreadyExistException("브랜드가 이미 존재: " + request.getBrand());
        }

        List<Coordi> coordis = convertDtoToEntity(request);
        coordiRepository.saveAll(coordis);
    }

    private void update(CoordiDto.RequestDto request) {
        if (CollectionUtils.isEmpty(request.getPrices())) {
            throw new InvalidDataException("카테고리 부족: " + request.getPrices().size());
        }

        boolean exist = coordiRepository.existsByBrand(request.getBrand());

        if (!exist) {
            throw new AlreadyExistException("수정할 브랜드가 없음: " + request.getBrand());
        }

        List<Coordi> newCoordis = convertDtoToEntity(request);
        List<Category> categories = newCoordis.stream()
                .map(Coordi::getCategory)
                .toList();
        List<Coordi> savedCoordis = coordiRepository.findAllByBrandAndCategoryIn(request.getBrand(), categories);
        Map<Category, Integer> priceMap = newCoordis.stream().collect(Collectors.toMap(Coordi::getCategory, Coordi::getPrice));
        for (Coordi coordi : savedCoordis) {
            coordi.setPrice(priceMap.get(coordi.getCategory()));
        }
    }

    private List<Coordi> convertDtoToEntity(CoordiDto.RequestDto request) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        List<Coordi> coordis = new ArrayList<>();
        for (CoordiDto.CategoryPriceDto categoryPriceDto : request.getPrices()) {
            Coordi coordi;
            try {
                coordi = Coordi.builder()
                        .brand(request.getBrand())
                        .category(Category.of(categoryPriceDto.getCategory()))
                        .price(numberFormat.parse(categoryPriceDto.getPrice()).intValue())
                        .build();
            } catch (ParseException e) {
                throw new InvalidDataException("가격 오류: " + categoryPriceDto.getPrice());
            }
            coordis.add(coordi);
        }

        return coordis;
    }

    private void delete(CoordiDto.RequestDto request) {
        boolean exist = coordiRepository.existsByBrand(request.getBrand());

        if (!exist) {
            throw new AlreadyExistException("삭제할 브랜드가 없음: " + request.getBrand());
        }

        coordiRepository.deleteAllByBrand(request.getBrand());
    }
}
