package com.ex.emailapi.repositories;

import com.ex.emailapi.entities.DailyRecipeTracker;
import com.ex.emailapi.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for daily recipe trackers data
 */

@Repository
public interface DailyRecipeTrackerRepository extends JpaRepository<DailyRecipeTracker, Integer> {
    List<DailyRecipeTracker> findAllByEmail(String email);
}
