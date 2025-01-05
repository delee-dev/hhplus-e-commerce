package kr.hhplus.be.server.api.point.controller;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.api.point.dto.ChargePointRequest;
import kr.hhplus.be.server.api.point.dto.ChargePointResponse;
import kr.hhplus.be.server.api.point.dto.GetPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
public class PointController {
    @GetMapping
    public ResponseEntity<GetPointResponse> getPoint(@RequestParam long userId) {
        if (userId == 9) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(new GetPointResponse(userId, "홍길동", 10_000));
    }

    @PatchMapping("/charge")
    public ResponseEntity<ChargePointResponse> charge(@RequestBody ChargePointRequest request) {
        if (request.amount() < 1_000) {
            throw new IllegalArgumentException("최소 충전 금액(1,000원)보다 작은 금액으로 충전할 수 없습니다.");
        } else if (request.amount() > 1_000_000) {
            throw new IllegalArgumentException("최대 충전 금액(1,000,000원)보다 큰 금액으로 충전할 수 없습니다.");
        } else if (request.userId() == 9) {
            throw new IllegalStateException("포인트 한도(10,000,000원)를 초과하여 충전할 수 없습니다.");
        } else {
            return ResponseEntity.ok(new ChargePointResponse(request.userId(), "홍길동", request.amount()));
        }
    }
}
