package kr.hhplus.be.server.domain.coupon;

public interface CouponIssuanceManager {
    int decreaseAndGetQuantity(Long couponId);
    int increaseAndGetQuantity(Long couponId);
    int getQuantity(Long couponId);
    void initializeQuantity(Long couponId, int quantity);
    boolean saveIssuedUser(Long couponId, Long userId);
}