package kr.hhplus.be.server.domain.member.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
