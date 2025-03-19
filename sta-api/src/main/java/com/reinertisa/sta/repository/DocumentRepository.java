package com.reinertisa.sta.repository;

import com.reinertisa.sta.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import static com.reinertisa.sta.constant.Constants.SELECT_COUNT_DOCUMENTS_QUERY;
import static com.reinertisa.sta.constant.Constants.SELECT_DOCUMENTS_QUERY;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    @Query(countQuery = SELECT_COUNT_DOCUMENTS_QUERY, value = SELECT_DOCUMENTS_QUERY, nativeQuery = true)
    Page<DocumentEntity> findDocuments(Pageable pageable);
}
