package kr.hhplus.be.server.api.point.controller;

import kr.hhplus.be.server.api.point.dto.ChargePointRequest;
import kr.hhplus.be.server.api.point.dto.ChargePointResponse;
import kr.hhplus.be.server.api.point.dto.GetPointResponse;
import kr.hhplus.be.server.application.point.PointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController implements PointSwaggerApiSpec {
    private final PointFacade pointFacade;

    @Override
    @GetMapping
    public ResponseEntity<GetPointResponse> getPoint(@RequestParam long userId) {
        GetPointResponse response = GetPointResponse.from(pointFacade.getPoint(userId));
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/charge")
    public ResponseEntity<ChargePointResponse> charge(@RequestBody ChargePointRequest request) {
        ChargePointResponse response = ChargePointResponse.from(pointFacade.charge(request.userId(), request.amount()));
        return ResponseEntity.ok(response);
    }
}
