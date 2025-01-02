package kr.hhplus.be.server.domain.point.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.model.User;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "points")
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long balance;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
