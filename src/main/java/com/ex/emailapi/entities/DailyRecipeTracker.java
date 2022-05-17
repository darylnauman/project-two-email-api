package com.ex.emailapi.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name= "DailyRecipeTracker")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DailyRecipeTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="trackerId", columnDefinition = "AUTO_INCREMENT")
    private int trackerId;

    private String email;
    private int recipeId;
}
