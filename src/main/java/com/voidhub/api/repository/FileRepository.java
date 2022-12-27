package com.voidhub.api.repository;

import com.voidhub.api.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileData, UUID> {

    Optional<FileData> findByName(String name);

}
