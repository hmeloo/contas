package br.com.contas.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auth_request")
@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
