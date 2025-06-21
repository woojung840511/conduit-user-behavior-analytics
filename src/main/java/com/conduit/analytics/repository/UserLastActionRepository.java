package com.conduit.analytics.repository;

import com.conduit.analytics.entity.UserLastAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLastActionRepository extends JpaRepository<UserLastAction, Long> {


}
