package com.example.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public abstract class Ticket implements Comparable<Ticket> {
    //static field shared by all instances of the class
    private static int nextId = 0;

    private String assignee;
    private Set<String> tags = new HashSet<>();

    private int id;
    private String title;   
    private String description;
    private String requester;
    private Status status;
    private Priority priority;
    private LocalDateTime createdAt;

    public Ticket(String title,String description,String requester,Priority priority){
        this.id = ++nextId;
        this.title = title;
        this.description = description;
        this.requester = requester;
        this.status = Status.OPEN;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }

    //Short constructor
    public Ticket(String title,String requester){
        this(title,"Not Provided",requester,Priority.MEDIUM);
    }

    //Getters 
    public int getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRequester(){
        return requester;
    }

    public Status getStatus(){
        return status;
    }

    public Priority getPriority(){
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public String getAssignee() {
        return assignee;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }



    //Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public double ageInHours() {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        return duration.toMillis()/(1000.0 * 60 * 60);
    }


    public static int totalCreated(){
        return nextId;
    }

    // Abstract method to be implemented by subclasses
    public abstract double estimateEffortHours();


    public int compareTo(Ticket other){

        int priorityComparison =
            Integer.compare(
                    other.getPriority().ordinal(),
                    this.getPriority().ordinal()
            );
        if (priorityComparison != 0) {
            return priorityComparison;
        }

        return this.createdAt.compareTo(other.createdAt);
    }

      // String representation of the ticket
    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", requester='" + requester + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

}
