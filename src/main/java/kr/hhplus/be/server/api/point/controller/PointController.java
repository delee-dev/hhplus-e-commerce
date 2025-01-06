package kr.hhplus.be.server.api.point.controller;

import kr.hhplus.be.server.api.point.dto.ChargePointRequest;
import kr.hhplus.be.server.api.point.dto.ChargePointResponse;
import kr.hhplus.be.server.api.point.dto.GetPointResponse;
import kr.hhplus.be.server.domain.point.PointErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
public class PointController {
    @GetMapping
    public ResponseEntity<GetPointResponse> getPoint(@RequestParam long userId) {
        if (userId == 9) {
            throw new DomainException(PointErrorCode.POINT_BALANCE_NOT_FOUND);
        }
        return ResponseEntity.ok(new GetPointResponse(userId, "홍길동", 10_000));
    }

    @PatchMapping("/charge")
    public ResponseEntity<ChargePointResponse> charge(@RequestBody ChargePointRequest request) {
        if (request.amount() < 1_000) {
            throw new DomainException(PointErrorCode.POINT_CHARGE_BELOW_MINIMUM);
        } else if (request.amount() > 1_000_000) {
            throw new DomainException(PointErrorCode.POINT_CHARGE_EXCEEDS_MAXIMUM);
        } else if (request.userId() == 9) {
            throw new DomainException(PointErrorCode.POINT_BALANCE_EXCEEDS_LIMIT);
        } else {
            return ResponseEntity.ok(new ChargePointResponse(request.userId(), "홍길동", request.amount()));
        }
    }
}
