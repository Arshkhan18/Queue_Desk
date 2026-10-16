package com.example.model;

public class FeatureRequestTicket extends Ticket {

    private int votes;
    private String businessJustification;

    public FeatureRequestTicket(
            String title,
            String description,
            String requester,
            Priority priority,
            int votes,
            String businessJustification) {

        super(title, description, requester, priority);

        this.votes = votes;
        this.businessJustification = businessJustification;
    }

    public int getVotes() {
        return votes;
    }

    public String getBusinessJustification() {
        return businessJustification;
    }

   
    public double estimateEffortHours() {
        if (votes >= 100) {
            return 20.0;
        } else if (votes >= 50) {
            return 12.0;
        } else if (votes >= 10) {
            return 6.0;
        }

        return 3.0;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", votes=" + votes +
                ", businessJustification='" +
                businessJustification + '\'' +
                '}';
    }
}