package com.example.util;

import com.example.model.Ticket;

import java.util.List;

public class TicketUtils {

    public static <T extends Ticket> T oldest(List<T> tickets) {

        if (tickets == null || tickets.isEmpty()) {
            return null;
        }

        T oldestTicket = tickets.get(0);

        for (T ticket : tickets) {

            if (ticket.getCreatedAt()
                    .isBefore(oldestTicket.getCreatedAt())) {

                oldestTicket = ticket;
            }
        }

        return oldestTicket;
    }
}