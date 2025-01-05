package kr.hhplus.be.server.api.coupon.controller;

import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @PostMapping("/issue")
    public ResponseEntity<IssueCouponResponse> issue(@RequestBody IssueCouponRequest request) {
        if (request.userId() == 9) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        } else if (request.userId() == 99) {
            throw new IllegalStateException("이미 쿠폰이 발급되었습니다.");
        } else if (request.userId() == 999) {
            throw new IllegalStateException("이미 쿠폰 발급 대기 중입니다.");
        } else {
            return ResponseEntity.ok(new IssueCouponResponse(true));
        }
    }
}
