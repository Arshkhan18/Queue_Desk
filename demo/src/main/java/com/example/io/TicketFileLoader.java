package com.example.io;

import com.example.exception.InvalidTicketDataException;
import com.example.model.AccessRequestTicket;
import com.example.model.BugTicket;
import com.example.model.FeatureRequestTicket;
import com.example.model.Priority;
import com.example.model.Severity;
import com.example.model.Status;
import com.example.model.Ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TicketFileLoader {

    public static List<Ticket> load(Path file)
            throws IOException, InvalidTicketDataException {

        List<Ticket> tickets = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                // Ignore blank lines
                if (line.isBlank()) {
                    continue;
                }

                try {
                    Ticket ticket = parseTicket(line);
                    tickets.add(ticket);

                } catch (RuntimeException e) {

                    throw new InvalidTicketDataException(
                            "Invalid ticket data at line " +
                            lineNumber + ": " + e.getMessage(),
                            e
                    );
                }
            }
        }

        return tickets;
    }

    private static Ticket parseTicket(String line) {

        String[] fields = line.split("\\|", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length != 6) {
            throw new IllegalArgumentException(
                    "Expected 6 fields but found " + fields.length
            );
        }

        String type = fields[0];
        String title = fields[1];
        String description = fields[2];
        String requester = fields[3];

        Priority priority = Priority.valueOf(fields[4]);
        Status status = Status.valueOf(fields[5]);

        if (title.isBlank() || requester.isBlank()) {
            throw new IllegalArgumentException(
                    "Title and requester cannot be blank"
            );
        }

        Ticket ticket;

        switch (type) {

            case "BUG":

                ticket = new BugTicket(
                        title,
                        description,
                        requester,
                        priority,
                        Severity.MEDIUM,
                        "Not provided"
                );

                break;

            case "FEATURE":

                ticket = new FeatureRequestTicket(
                        title,
                        description,
                        requester,
                        priority,
                        0,
                        "Not provided"
                );

                break;

            case "ACCESS":

                ticket = new AccessRequestTicket(
                        title,
                        description,
                        requester,
                        priority,
                        "Not specified",
                        "READ_ONLY"
                );

                break;

            default:

                throw new IllegalArgumentException(
                        "Unknown ticket type: " + type
                );
        }

        ticket.setStatus(status);

        return ticket;
    }
}