package kr.hhplus.be.server.scheduler.product;

import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BestSellerCacheScheduler {
    private final ProductService productService;
    private final CacheManager cacheManager;

    @Scheduled(cron = "0 0 0 * * *")
    public void refreshBestSellerCache() {
        Optional.ofNullable(cacheManager.getCache("bestSellers"))
                .ifPresent(cache -> {
                    List<Long> categoryIds = productService.getAllCategoryIds();
                    categoryIds.forEach(productService::refreshBestSellingProducts);
                });
    }
}
