package kr.hhplus.be.server.api.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.SaleStatus;

import java.util.List;

@Schema(description = "상품 조회 응답 DTO")
public record ProductSummaryResponse(
        @Schema(description = "상품 ID", example = "1")
        long id,
        @Schema(description = "상품명", example = "로봇청소기 Pro")
        String name,
        @Schema(description = "상품 설명", example = "반려동물 털 청소에 최적화된 신상 로봇청소기입니다")
        String description,
        @Schema(description = "카테고리명", example = "가전")
        String category,
        @Schema(description = "가격", example = "399000")
        long price,
        @Schema(description = "상품 판매 상태", example = "ON_SALE")
        SaleStatus status
) {
        public static ProductSummaryResponse from(ProductResult domainDto) {
                return new ProductSummaryResponse(
                        domainDto.id(),
                        domainDto.name(),
                        domainDto.description(),
                        domainDto.category(),
                        domainDto.price(),
                        domainDto.status()
                );
        }

        public static List<ProductSummaryResponse> from(List<ProductResult> domainDtos) {
                return domainDtos.stream()
                        .map(ProductSummaryResponse::from)
                        .toList();
        }
}
