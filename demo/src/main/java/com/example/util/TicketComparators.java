package com.example.util;

import com.example.model.Ticket;
import java.util.Comparator;

public class TicketComparators {
    //sorting by assignee and then by priority
    public static Comparator<Ticket> byAssigneeThenPriority(){
        return Comparator.comparing(
            Ticket::getAssignee,Comparator.nullsLast(Comparator.naturalOrder())
        ).thenComparing(
            Ticket::getPriority,Comparator.nullsLast(Comparator.naturalOrder())
        );
    }

    //sorting by status and then by age
    public static Comparator<Ticket> byStatusThenAge(){
        return Comparator.comparing(Ticket::getStatus)
            .thenComparing(Ticket::getCreatedAt);
    }
}
