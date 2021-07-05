package com.rsoi.model;

import com.rsoi.security.Encryptor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name="users")
public class User
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Column(unique=true)
    @NonNull
    private UUID userUid = UUID.randomUUID();
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    @Convert(converter = Encryptor.class)
    private String password;
    @Getter
    @Setter
    private GrantedAuthority grantedAuthorities;

}
