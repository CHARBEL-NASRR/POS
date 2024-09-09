package com.example.projectt;

public class feedback {
    private int feedbackId;
    private String name;
    private String email;
    private int rank;
    private String comment;
    private String selectedCheckbox;

    public feedback(int feedbackId, String name, String email, int rank, String comment, String selectedCheckbox) {
        this.feedbackId = feedbackId;
        this.name = name;
        this.email = email;
        this.rank = rank;
        this.comment = comment;
        this.selectedCheckbox = selectedCheckbox;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSelectedCheckbox() {
        return selectedCheckbox;
    }

    public void setSelectedCheckbox(String selectedCheckbox) {
        this.selectedCheckbox = selectedCheckbox;
    }
}
