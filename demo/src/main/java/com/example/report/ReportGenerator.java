package com.example.report;

import com.example.model.Priority;
import com.example.model.Status;
import com.example.model.Ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    private static final double OVERDUE_HOURS = 24.0;

    public static String generate(List<Ticket> tickets) {

        StringBuilder report = new StringBuilder();

        report.append("========== QUEUEDESK END-OF-DAY REPORT ==========\n");
        report.append("Generated at: ")
                .append(LocalDateTime.now())
                .append("\n\n");

        // Count by status
        Map<Status, Integer> statusCounts =
                new EnumMap<>(Status.class);

        for (Status status : Status.values()) {
            statusCounts.put(status, 0);
        }

        // Count by priority
        Map<Priority, Integer> priorityCounts =
                new EnumMap<>(Priority.class);

        for (Priority priority : Priority.values()) {
            priorityCounts.put(priority, 0);
        }

        for (Ticket ticket : tickets) {
            statusCounts.merge(ticket.getStatus(), 1, Integer::sum);
            priorityCounts.merge(ticket.getPriority(), 1, Integer::sum);
        }

        report.append("TOTAL TICKETS: ")
                .append(tickets.size())
                .append("\n\n");

        report.append("COUNT BY STATUS:\n");

        for (Status status : Status.values()) {
            report.append(status)
                    .append(": ")
                    .append(statusCounts.get(status))
                    .append("\n");
        }

        report.append("\nCOUNT BY PRIORITY:\n");

        for (Priority priority : Priority.values()) {
            report.append(priority)
                    .append(": ")
                    .append(priorityCounts.get(priority))
                    .append("\n");
        }

        // Find oldest open ticket
        Ticket oldestOpen = null;

        for (Ticket ticket : tickets) {

            if (ticket.getStatus() == Status.OPEN) {

                if (oldestOpen == null ||
                        ticket.getCreatedAt()
                                .isBefore(oldestOpen.getCreatedAt())) {

                    oldestOpen = ticket;
                }
            }
        }

        report.append("\nOLDEST OPEN TICKET:\n");

        if (oldestOpen == null) {

            report.append("No open tickets.\n");

        } else {

            report.append("Ticket #")
                    .append(oldestOpen.getId())
                    .append(" - ")
                    .append(oldestOpen.getTitle())
                    .append("\n");
        }

        // Find overdue CRITICAL tickets
        report.append("\nOVERDUE CRITICAL TICKETS:\n");

        boolean foundOverdue = false;

        for (Ticket ticket : tickets) {

            boolean isCritical =
                    ticket.getPriority() == Priority.CRITICAL;

            boolean isOpen =
                    ticket.getStatus() == Status.OPEN ||
                    ticket.getStatus() == Status.IN_PROGRESS;

            boolean isOverdue =
                    ticket.ageInHours() > OVERDUE_HOURS;

            if (isCritical && isOpen && isOverdue) {

                foundOverdue = true;

                report.append("Ticket #")
                        .append(ticket.getId())
                        .append(" - ")
                        .append(ticket.getTitle())
                        .append(" (")
                        .append(String.format(
                                "%.2f",
                                ticket.ageInHours()
                        ))
                        .append(" hours old)\n");
            }
        }

        if (!foundOverdue) {
            report.append("No overdue critical tickets.\n");
        }

        return report.toString();
    }

    public static void save(String report, Path outputFile)
            throws IOException {

        try (BufferedWriter writer =
                     Files.newBufferedWriter(outputFile)) {

            writer.write(report);
        }
    }
}