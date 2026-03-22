package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name="token_register")
@Setter
@Getter
public class TokenRegisterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_register_id")
    private Integer id;

    @Column(name="token")
    private String token;
    @Column(name="expiration_date")
    private Date expirationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable=false)
    private UsersEntity user;
}
