package kr.hhplus.be.server.infrastructure.coupon.store;

import kr.hhplus.be.server.domain.coupon.CouponIssuanceManager;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCouponIssuanceManager implements CouponIssuanceManager {
    private final RedissonClient redissonClient;

    private static final String COUPON_QUANTITY_PREFIX = "coupon:quantity";
    private static final String ISSUED_USER_PREFIX = "coupon:issued";

    @Override
    public int decreaseAndGetQuantity(Long couponId) {
        return (int) getQuantityAtomicLong(couponId).decrementAndGet();
    }

    @Override
    public int increaseAndGetQuantity(Long couponId) {
        return (int) getQuantityAtomicLong(couponId).incrementAndGet();
    }

    @Override
    public int getQuantity(Long couponId) {
        return (int) getQuantityAtomicLong(couponId).get();
    }

    @Override
    public void initializeQuantity(Long couponId, int quantity) {
        getQuantityAtomicLong(couponId).set(quantity);
    }

    private RAtomicLong getQuantityAtomicLong(Long couponId) {
        String key = String.join(":", COUPON_QUANTITY_PREFIX, couponId.toString());
        return redissonClient.getAtomicLong(key);
    }

    @Override
    public boolean saveIssuedUser(Long couponId, Long userId) {
        RSet<Long> issuedUserSet = getIssuedUserSet(couponId);
        return issuedUserSet.add(userId);
    }

    private RSet<Long> getIssuedUserSet(Long couponId) {
        String key = String.join(":", ISSUED_USER_PREFIX, couponId.toString());
        return redissonClient.getSet(key);
    }
}
