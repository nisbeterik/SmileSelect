package com.smile_select.logging_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.smile_select.logging_service.model.LogEntry;

@Repository
public interface LogRepository extends MongoRepository<LogEntry, String> {

}
