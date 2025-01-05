package kr.hhplus.be.server.api.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.product.dto.ProductSummaryResponse;
import kr.hhplus.be.server.global.model.PageResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Product API", description = "상품 API")
public interface ProductSwaggerApiSpec {
    @Operation(
            summary = "상품 리스트 조회",
            description = "카테고리 ID, 페이지 정보, 정렬 정보를 입력 받아 상품 리스트를 조회합니다.",
            tags = {"Product API"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 리스트 조회 성공",
            content = @Content(
                    schema = @Schema(
                            implementation = PageResponse.class,
                            subTypes = {ProductSummaryResponse.class}
                    ),
                    examples = @ExampleObject(
                            value = """
                                {
                                    "content": [
                                        {
                                            "userId": 1,
                                            "name": "로봇청소기 Pro",
                                            "description": "반려동물 털 청소에 최적화된 신상 로봇청소기입니다",
                                            "category": "가전",
                                            "price": 399000,
                                            "status": "ON_SALE"
                                        },
                                        ...
                                    ],
                                    "pageInfo": {
                                        "page": 0,
                                        "size": 10,
                                        "totalElements": 42,
                                        "totalPages": 5
                                    }
                                }
                            """)
            )
    )
    @Parameters({
            @Parameter(name = "categoryId", required = true, description = "카테고리 ID"),
            @Parameter(name = "page", required = true, description = "페이지"),
            @Parameter(name = "size", required = true, description = "페이지 사이즈"),
            @Parameter(name = "sort", required = false, description = "정렬 기준 컬럼"),
            @Parameter(name = "direction", required = false, description = "정렬 방향(오름차순, 내림차순)"),
    })
    ResponseEntity<PageResponse<ProductSummaryResponse>> getProductsByCategory(long categoryId, int page, int size, String sort, String direction);

    @Operation(
            summary = "상위 판매 상품(베스트 셀러) 조회",
            description = "카테고리 ID를 입력 받아 상위 판매 상품(베스트 셀러)을 조회합니다.",
            tags = {"Product API"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "상위 판매 상품(베스트 셀러) 조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductSummaryResponse.class))
            ))
    @Parameter(name = "categoryId", required = true, description = "카테고리 ID")
    ResponseEntity<List<ProductSummaryResponse>> getBestSellersByCategory(long categoryId);
}
