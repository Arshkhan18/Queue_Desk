package com.example.repository;

import com.example.exception.TicketNotFoundException;
import com.example.model.Ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository<T extends Ticket> {

    private final List<T> tickets = new ArrayList<>();

    // Add a ticket
    public void add(T ticket) {
        tickets.add(ticket);
    }

    // Return an unmodifiable view
    public List<T> all() {
        return Collections.unmodifiableList(tickets);
    }

    // Find a ticket by ID
    public T findById(int id) {

        for (T ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }

        throw new TicketNotFoundException(
                "Ticket with ID " + id + " was not found"
        );
    }

    // Group tickets by assignee
    public Map<String, List<T>> groupByAssignee() {

        Map<String, List<T>> grouped = new HashMap<>();

        for (T ticket : tickets) {

            String assignee = ticket.getAssignee();

            if (assignee == null || assignee.isBlank()) {
                assignee = "Unassigned";
            }

            grouped
                    .computeIfAbsent(
                            assignee,
                            key -> new ArrayList<>()
                    )
                    .add(ticket);
        }

        return grouped;
    }
}