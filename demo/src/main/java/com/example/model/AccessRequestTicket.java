package com.example.model;

import com.example.capability.Escalatable;

public class AccessRequestTicket extends Ticket implements Escalatable {

    private String systemName;
    private String accessLevel;

    private int escalationLevel = 0;

    public AccessRequestTicket(
            String title,
            String description,
            String requester,
            Priority priority,
            String systemName,
            String accessLevel) {

        super(title, description, requester, priority);

        this.systemName = systemName;
        this.accessLevel = accessLevel;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getAccessLevel() {
        return accessLevel;
    }


    public double estimateEffortHours(){
        if("ADMIN".equalsIgnoreCase(accessLevel)){
            return 8.0;
        } else if("WRITE".equalsIgnoreCase(accessLevel)){
            return 4.0;
        } else if("READ_ONLY".equalsIgnoreCase(accessLevel)){
            return 2.0;
        }
        return 3.0;
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

    @Override
    public String toString() {
        return super.toString() +
                ", systemName='" + systemName + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                '}';
    }

}