package com.example.model;
import com.example.capability.Escalatable;

public class BugTicket extends Ticket implements Escalatable {
    private Severity severity;
    private String stepsToReproduce;

    private int escalationLevel=0;

      public BugTicket(
            String title,
            String description,
            String requester,
            Priority priority,
            Severity severity,
            String stepsToReproduce) {

        super(title, description, requester, priority);

        this.severity = severity;
        this.stepsToReproduce = stepsToReproduce;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getStepsToReproduce() {
        return stepsToReproduce;
    }

    public double estimateEffortHours(){
         switch (severity) {
            case LOW:
                return 1.0;
            case MEDIUM:
                return 3.0;
            case HIGH:
                return 6.0;
            case CRITICAL:
                return 10.0;
            default:
                throw new IllegalStateException("Unknown severity");
        }
    }

    public void escalate() {
        escalationLevel++;
    }

    public boolean isEscalated() {
        return escalationLevel > 0;
    }

    public int escalationLevel() {
        return escalationLevel;
    }

    public String toString() {
        return super.toString() +
                ", severity=" + severity +
                ", stepsToReproduce='" + stepsToReproduce + '\'' +
                '}';
    }
}
