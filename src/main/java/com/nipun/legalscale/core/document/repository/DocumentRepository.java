package com.nipun.legalscale.core.document.repository;

import com.nipun.legalscale.core.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
