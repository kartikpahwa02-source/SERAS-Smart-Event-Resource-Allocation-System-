package com.seras.model;

public class Event {

    private int id;
    private String eventName;
    private int resourceId;
    private String date;
    private String startTime;
    private String endTime;

    public Event(int id, String eventName, int resourceId, String date, String startTime, String endTime) {
        this.id = id;
        this.eventName = eventName;
        this.resourceId = resourceId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() { return id; }
    public String getEventName() { return eventName; }
    public int getResourceId() { return resourceId; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}