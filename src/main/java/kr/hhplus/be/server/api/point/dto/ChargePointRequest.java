package kr.hhplus.be.server.api.point.dto;

public record ChargePointRequest(
        long userId,
        long amount
){
}
