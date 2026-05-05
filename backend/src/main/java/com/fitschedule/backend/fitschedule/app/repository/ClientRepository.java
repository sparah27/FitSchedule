package com.fitschedule.backend.fitschedule.app.repository;

import com.fitschedule.backend.fitschedule.app.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}