package com.reinertisa.sta.repository;

import com.reinertisa.sta.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    @Query(countQuery = "SELECT COUNT(*) FROM documents", value = "", nativeQuery = true)
    Page<DocumentEntity> findDocuments(Pageable pageable);
}
