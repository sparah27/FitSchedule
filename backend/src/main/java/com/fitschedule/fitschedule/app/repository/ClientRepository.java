package com.fitschedule.fitschedule.app.repository;

import com.fitschedule.fitschedule.app.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}