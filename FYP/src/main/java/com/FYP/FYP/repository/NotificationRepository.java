package com.FYP.FYP.repository;

import com.FYP.FYP.model.Notification;
import com.FYP.FYP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserAndIsReadFalse(User user);
}
