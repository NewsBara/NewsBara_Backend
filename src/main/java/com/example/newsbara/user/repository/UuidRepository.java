package com.example.newsbara.user.repository;


import com.example.newsbara.user.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
