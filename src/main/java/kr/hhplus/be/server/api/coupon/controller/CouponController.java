package kr.hhplus.be.server.api.coupon.controller;

import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponResponse;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController implements CouponSwaggerApiSpec {
    private final CouponService couponService;

    @Override
    @PostMapping("/issue")
    public ResponseEntity<IssueCouponResponse> issue(@RequestBody IssueCouponRequest request) {
        IssueCouponResponse response = IssueCouponResponse.fromApp(couponService.issueWithLock(request.toApp()));

        return ResponseEntity
                .status(201)
                .body(response);
    }
}
