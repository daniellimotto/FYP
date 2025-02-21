package com.FYP.FYP.repository;

import com.FYP.FYP.model.ChatMessage;
import com.FYP.FYP.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByTaskOrderBySentAtAsc(Task task);
}
