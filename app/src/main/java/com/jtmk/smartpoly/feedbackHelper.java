package com.jtmk.smartpoly;

public class feedbackHelper {
    public feedbackHelper(String rating, String feedback) {
        this.rating = rating;
        this.feedback = feedback;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    String rating;
    String feedback;
}
