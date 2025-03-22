package com.FYP.FYP.repository;

import com.FYP.FYP.model.ChatSummary;
import com.FYP.FYP.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSummaryRepository extends JpaRepository<ChatSummary, Integer> {
    Optional<ChatSummary> findByTask(Task task);
}
