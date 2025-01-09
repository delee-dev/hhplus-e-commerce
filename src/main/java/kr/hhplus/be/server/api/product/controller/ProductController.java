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
        List<ProductSummaryResponse> responses = ProductSummaryResponse.fromDomains(productService.getBestSellingProducts(categoryId));
        return ResponseEntity.ok(responses);
    }
}
