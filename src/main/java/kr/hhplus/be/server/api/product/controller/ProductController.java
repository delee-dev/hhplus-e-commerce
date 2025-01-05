package kr.hhplus.be.server.api.product.controller;

import kr.hhplus.be.server.api.product.dto.ProductSummaryResponse;
import kr.hhplus.be.server.api.product.dto.SortColumn;
import kr.hhplus.be.server.api.product.dto.SortDirection;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.global.model.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @GetMapping(value = "/list")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProductsByCategory(
            @RequestParam long categoryId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "CREATED_AT") SortColumn sort,
            @RequestParam(defaultValue = "ASC") SortDirection direction
    ) {
        if (categoryId == 9) {
            return ResponseEntity.ok(new PageResponse<>(List.of(), new PageResponse.PageInfo(0, 10, 0, 0)));
        }
        return ResponseEntity.ok(new PageResponse<>(
                List.of(
                        new ProductSummaryResponse(1L, "로봇청소기 Pro", "반려동물 털 청소에 최적화된 신상 로봇청소기입니다", "가전", 399000L, 599000L, 100, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(2L, "울트라 게이밍 마우스", "초고성능 게이밍 마우스", "PC주변기기", 89000L, 129000L, 50, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(3L, "프리미엄 블루투스 이어폰", "고음질 무선 이어폰", "음향기기", 159000L, 200000L, 0, SaleStatus.TEMPORARILY_OUT),
                        new ProductSummaryResponse(4L, "스마트 LED 모니터", "눈이 편한 게이밍 모니터", "모니터", 429000L, 599000L, 25, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(5L, "기계식 게이밍 키보드", "청축 스위치 적용", "PC주변기기", 129000L, 159000L, 30, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(6L, "게이밍 의자 DX", "12시간 앉아도 편한 의자", "가구", 299000L, 399000L, 15, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(7L, "4K 웹캠", "선명한 화질의 웹캠", "PC주변기기", 89000L, 129000L, 0, SaleStatus.TEMPORARILY_OUT),
                        new ProductSummaryResponse(8L, "무선 충전 마우스패드", "고급 가죽 재질", "PC주변기기", 59000L, 79000L, 40, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(9L, "게이밍 헤드셋 Pro", "7.1 서라운드 사운드", "음향기기", 189000L, 229000L, 20, SaleStatus.ON_SALE),
                        new ProductSummaryResponse(10L, "듀얼 모니터 스탠드", "알루미늄 재질 스탠드", "가구", 99000L, 149000L, 35, SaleStatus.ON_SALE)
                ),
                new PageResponse.PageInfo(
                        0,
                        10,
                        42,
                        5
                )
        ));
    }

    @GetMapping("/best")
    public ResponseEntity<List<ProductSummaryResponse>> getBestSellersByCategory(@RequestParam long categoryId) {
        return ResponseEntity.ok(List.of(
                new ProductSummaryResponse(2L, "울트라 게이밍 마우스", "초고성능 게이밍 마우스", "PC주변기기", 89000L, 129000L, 50, SaleStatus.ON_SALE),
                new ProductSummaryResponse(15L, "스피커", "사운드 빵빵한 스피커", "PC주변기기", 130000L, 145000L, 0, SaleStatus.TEMPORARILY_OUT),
                new ProductSummaryResponse(8L, "무선 충전 마우스패드", "고급 가죽 재질", "PC주변기기", 59000L, 79000L, 40, SaleStatus.ON_SALE),
                new ProductSummaryResponse(5L, "기계식 게이밍 키보드", "청축 스위치 적용", "PC주변기기", 129000L, 159000L, 30, SaleStatus.ON_SALE),
                new ProductSummaryResponse(7L, "4K 웹캠", "선명한 화질의 웹캠", "PC주변기기", 89000L, 129000L, 0, SaleStatus.TEMPORARILY_OUT)
        ));
    }
}
