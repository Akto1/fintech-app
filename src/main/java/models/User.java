package models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 10)
    private String username;

    @Column(nullable = false,unique = true,updatable = true,length = 30)
    private String email;

    @Column(unique = true,updatable = true,nullable = false,length = 15)
    private String password;

    @Column(nullable = false,updatable = true)
    private BigDecimal balance = BigDecimal.ZERO;

}
