package kr.hhplus.be.server.api.coupon.controller;

import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponResponse;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController implements CouponSwaggerApiSpec {
    private final CouponService couponService;

    @Override
    @PostMapping("/issue")
    public ResponseEntity<IssueCouponResponse> issue(@RequestBody IssueCouponRequest request) {
        IssueCouponResponse response = IssueCouponResponse.from(couponService.issue(request.to()));

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @Override
    @PostMapping("/quantity/initialize/{couponId}")
    public ResponseEntity<Void> initializeQuantity(@PathVariable Long couponId) {
        // for admin
        couponService.initializeCouponQuantity(couponId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
