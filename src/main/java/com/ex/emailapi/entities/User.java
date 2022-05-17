package com.ex.emailapi.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name= "User")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String email;
    private String userPassword;
    private int isLoggedIn;
    private int subscriptionStatus;

}
