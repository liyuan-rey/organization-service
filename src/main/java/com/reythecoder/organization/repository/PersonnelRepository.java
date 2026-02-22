package com.reythecoder.organization.repository;

import com.reythecoder.organization.entity.PersonnelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonnelRepository extends JpaRepository<PersonnelEntity, UUID> {
}