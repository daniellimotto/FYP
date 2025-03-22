package com.FYP.FYP.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "chat_summaries")
public class ChatSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    
    @Column(name = "message_count")
    private int messageCount;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public int getMessageCount() {
        return messageCount;
    }
    
    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}