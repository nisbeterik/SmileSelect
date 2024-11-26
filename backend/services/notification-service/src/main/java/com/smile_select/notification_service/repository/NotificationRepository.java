package com.smile_select.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smile_select.notification_service.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{
    
}
