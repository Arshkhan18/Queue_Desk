package com.example;

import com.example.capability.Escalatable;
import com.example.exception.InvalidTicketDataException;
import com.example.exception.TicketNotFoundException;
import com.example.io.TicketFileLoader;
import com.example.model.AccessRequestTicket;
import com.example.model.BugTicket;
import com.example.model.FeatureRequestTicket;
import com.example.model.Priority;
import com.example.model.Severity;
import com.example.model.Ticket;
import com.example.report.ReportGenerator;
import com.example.repository.Repository;
import com.example.util.TicketComparators;
import com.example.util.TicketUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class App {

    public static void main(String[] args) {

        try {
            runApplication();
        } catch (TicketNotFoundException e) {
            System.err.println("Ticket error: " + e.getMessage());
        } catch (InvalidTicketDataException e) {
            System.err.println("Invalid ticket data: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File operation failed: " + e.getMessage());
        }
    }

    public static void runApplication()
            throws IOException, InvalidTicketDataException {

        // ==========================================
        // 1. CREATE SAMPLE TICKETS
        // ==========================================

        List<Ticket> tickets = new ArrayList<>();

        BugTicket bug = new BugTicket(
                "Login page crashes",
                "Application crashes after login",
                "Rahul",
                Priority.CRITICAL,
                Severity.CRITICAL,
                "Open login page and enter credentials"
        );

        FeatureRequestTicket feature = new FeatureRequestTicket(
                "Add dark mode",
                "Provide a dark theme",
                "Priya",
                Priority.MEDIUM,
                120,
                "Improve user experience"
        );

        AccessRequestTicket access = new AccessRequestTicket(
                "Database access",
                "Request read-only access",
                "Aman",
                Priority.HIGH,
                "Production Database",
                "READ_ONLY"
        );

        tickets.add(bug);
        tickets.add(feature);
        tickets.add(access);

        // ==========================================
        // 2. ESCALATION
        // ==========================================

        bug.escalate();
        access.escalate();

        // ==========================================
        // 3. ASSIGNEES AND TAGS
        // ==========================================

        bug.setAssignee("Rahul");
        feature.setAssignee("Priya");
        access.setAssignee("Rahul");

        bug.addTag("backend");
        bug.addTag("urgent");
        bug.addTag("backend");

        feature.addTag("enhancement");
        feature.addTag("ui");

        access.addTag("database");
        access.addTag("security");

        // ==========================================
        // 4. DISPLAY TRIAGE BOARD
        // ==========================================

        printTriageBoard(tickets);

        // ==========================================
        // 5. REPOSITORY
        // ==========================================

        System.out.println("\n========== REPOSITORY ==========");

        Repository<Ticket> repository = new Repository<>();

        for (Ticket ticket : tickets) {
            repository.add(ticket);
        }

        System.out.println(
                "Total tickets in repository: " +
                repository.all().size()
        );

        System.out.println("\nFinding ticket with ID 1:");

        System.out.println(repository.findById(bug.getId()));

        // ==========================================
        // 6. GROUP BY ASSIGNEE
        // ==========================================

        System.out.println("\n========== GROUP BY ASSIGNEE ==========");

        Map<String, List<Ticket>> groupedTickets =
                repository.groupByAssignee();

        for (Map.Entry<String, List<Ticket>> entry
                : groupedTickets.entrySet()) {

            System.out.println("\nAssignee: " + entry.getKey());

            for (Ticket ticket : entry.getValue()) {
                System.out.println(
                        "  Ticket #" + ticket.getId() +
                        " - " + ticket.getTitle()
                );
            }
        }

        // ==========================================
        // 7. DISPLAY TAGS
        // ==========================================

        System.out.println("\n========== TICKET TAGS ==========");

        for (Ticket ticket : tickets) {
            System.out.println(
                    ticket.getTitle() + " -> " + ticket.getTags()
            );
        }

        // ==========================================
        // 8. NATURAL-ORDER PRIORITY QUEUE
        // ==========================================

        System.out.println("\n========== NATURAL-ORDER QUEUE ==========");

        PriorityQueue<Ticket> triageQueue = new PriorityQueue<>();
        triageQueue.addAll(tickets);

        while (!triageQueue.isEmpty()) {
            Ticket nextTicket = triageQueue.poll();

            System.out.println(
                    "Serving Ticket #" + nextTicket.getId() +
                    " | Priority: " + nextTicket.getPriority() +
                    " | Title: " + nextTicket.getTitle()
            );
        }

        // ==========================================
        // 9. CUSTOM COMPARATOR QUEUE
        // ==========================================

        System.out.println(
                "\n========== QUEUE BY ASSIGNEE AND PRIORITY =========="
        );

        PriorityQueue<Ticket> customQueue =
                new PriorityQueue<>(
                        TicketComparators.byAssigneeThenPriority()
                );

        customQueue.addAll(tickets);

        while (!customQueue.isEmpty()) {
            Ticket ticket = customQueue.poll();

            System.out.println(
                    "Assignee: " + ticket.getAssignee() +
                    " | Priority: " + ticket.getPriority() +
                    " | Title: " + ticket.getTitle()
            );
        }

        // ==========================================
        // 10. SORT BY STATUS AND AGE
        // ==========================================

        System.out.println("\n========== SORT BY STATUS AND AGE ==========");

        PriorityQueue<Ticket> statusQueue =
                new PriorityQueue<>(
                        TicketComparators.byStatusThenAge()
                );

        statusQueue.addAll(tickets);

        while (!statusQueue.isEmpty()) {
            Ticket ticket = statusQueue.poll();

            System.out.println(
                    "Status: " + ticket.getStatus() +
                    " | Created: " + ticket.getCreatedAt() +
                    " | Title: " + ticket.getTitle()
            );
        }

        // ==========================================
        // 11. FIND OLDEST TICKET
        // ==========================================

        System.out.println("\n========== OLDEST TICKET ==========");

        Ticket oldestTicket = TicketUtils.oldest(tickets);

        if (oldestTicket != null) {
            System.out.println(
                    "Oldest ticket: #" + oldestTicket.getId() +
                    " - " + oldestTicket.getTitle()
            );
        } else {
            System.out.println("No tickets available.");
        }

        // ==========================================
        // 12. LOAD TICKETS FROM FILE
        // ==========================================

        System.out.println("\n========== LOAD FROM FILE ==========");

        Path inputFile = Path.of("demo", "tickets.txt");

        List<Ticket> loadedTickets = TicketFileLoader.load(inputFile);

        System.out.println(
                "Successfully loaded " + loadedTickets.size() +
                " tickets from " + inputFile
        );

        Repository<Ticket> loadedRepository = new Repository<>();

        for (Ticket ticket : loadedTickets) {
            loadedRepository.add(ticket);
        }

        printTriageBoard(loadedRepository.all());

        // ==========================================
        // 13. GENERATE AND SAVE REPORT
        // ==========================================

        System.out.println("\n========== GENERATE REPORT ==========");

        String report = ReportGenerator.generate(
                loadedRepository.all()
        );

        Path reportFile = Path.of("report.txt");

        ReportGenerator.save(report, reportFile);

        System.out.println(report);
        System.out.println("Report saved to: " + reportFile.toAbsolutePath());

        // ==========================================
        // 14. SUMMARY
        // ==========================================

        System.out.println("\n========== SUMMARY ==========");

        System.out.println(
                "Total tickets created during this run: " +
                Ticket.totalCreated()
        );
    }

    // ==========================================
    // TRIAGE BOARD
    // ==========================================

    public static void printTriageBoard(List<Ticket> tickets) {

        System.out.println("\n========== QUEUEDESK TRIAGE BOARD ==========");

        for (Ticket ticket : tickets) {

            System.out.println("\n" + ticket);

            System.out.println(
                    "Description: " +
                    truncate(ticket.getDescription(), 60)
            );

            System.out.println(
                    "Estimated effort: " +
                    ticket.estimateEffortHours() +
                    " hours"
            );

            if (ticket instanceof Escalatable escalatable) {
                System.out.println(escalatable.escalationBadge());
            }
        }

        System.out.println(
                "\nTotal tickets created: " + Ticket.totalCreated()
        );
    }

    // ==========================================
    // SAFE DESCRIPTION TRUNCATION
    // ==========================================

    private static String truncate(String text, int maxLength) {

        if (text == null) {
            return "";
        }

        text = text.trim();

        if (text.isBlank()) {
            return "(No description)";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }
}