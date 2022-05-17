package com.ex.emailapi.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name= "Subscription")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subscriptionId;

    private String email;
    private String preferences;

}
