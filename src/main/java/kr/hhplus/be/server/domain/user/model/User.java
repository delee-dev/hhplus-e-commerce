package kr.hhplus.be.server.domain.user.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
}
