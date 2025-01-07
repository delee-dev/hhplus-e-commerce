package kr.hhplus.be.server.api.product.controller;

import kr.hhplus.be.server.api.product.dto.ProductSummaryResponse;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.global.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController implements ProductSwaggerApiSpec {
    private final ProductService productService;

    @Override
    @GetMapping(value = "/list")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProductsByCategory(
            @RequestParam long categoryId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        GetProductsQuery query = new GetProductsQuery(categoryId, page, size, sort, direction);
        PageResponse<ProductResult> productPage = productService.getProductsByCategory(query);
        PageResponse<ProductSummaryResponse> response = productPage.map(ProductSummaryResponse::fromDomain);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/best")
    public ResponseEntity<List<ProductSummaryResponse>> getBestSellersByCategory(@RequestParam long categoryId) {
        return ResponseEntity.ok(List.of(
                new ProductSummaryResponse(2L, "울트라 게이밍 마우스", "초고성능 게이밍 마우스", "PC주변기기", 89000L, SaleStatus.ON_SALE),
                new ProductSummaryResponse(15L, "스피커", "사운드 빵빵한 스피커", "PC주변기기", 130000L, SaleStatus.TEMPORARILY_OUT),
                new ProductSummaryResponse(8L, "무선 충전 마우스패드", "고급 가죽 재질", "PC주변기기", 59000L, SaleStatus.ON_SALE),
                new ProductSummaryResponse(5L, "기계식 게이밍 키보드", "청축 스위치 적용", "PC주변기기", 129000L, SaleStatus.ON_SALE),
                new ProductSummaryResponse(7L, "4K 웹캠", "선명한 화질의 웹캠", "PC주변기기", 89000L, SaleStatus.TEMPORARILY_OUT)
        ));
    }
}
