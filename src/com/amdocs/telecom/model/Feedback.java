package com.amdocs.telecom.model;

public class Feedback {
    private int feedbackId;
    private int ticketId;
    private int customerId;
    private int rating; // 1 to 5
    private String comments;
    private String feedbackDate;

    public Feedback() {}

    public Feedback(int feedbackId, int ticketId, int customerId, int rating, String comments, String feedbackDate) {
        this.feedbackId = feedbackId;
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(String feedbackDate) { this.feedbackDate = feedbackDate; }
}
