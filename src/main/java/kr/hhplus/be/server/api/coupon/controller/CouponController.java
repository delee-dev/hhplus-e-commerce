package kr.hhplus.be.server.api.coupon.controller;

import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponResponse;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupon")
public class CouponController implements CouponSwaggerApiSpec {

    @Override
    @PostMapping("/issue")
    public ResponseEntity<IssueCouponResponse> issue(@RequestBody IssueCouponRequest request) {
        if (request.userId() == 9) {
            throw new DomainException(CouponErrorCode.COUPON_STOCK_DEPLETED);
        } else if (request.userId() == 99) {
            throw new DomainException(CouponErrorCode.COUPON_ALREADY_ISSUED);
        } else if (request.userId() == 999) {
            throw new DomainException(CouponErrorCode.COUPON_ISSUE_IN_PROGRESS);
        } else {
            return ResponseEntity
                    .status(201)
                    .body(new IssueCouponResponse(true));
        }
    }
}
