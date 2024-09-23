package com.example.myapp;

 class Bid {
    private String id;
    private String firstName;
    private String lastName;
    private String winnerEmail; // Assuming this is the email field
    private String location;
    private String educationLevel;
    private String experienceYears;
    private String amount;
    private String phoneNumber;

    // Default constructor required for calls to DataSnapshot.getValue(Bid.class)
    public Bid() {}

    public Bid(String id, String firstName, String lastName, String winnerEmail, String location, String educationLevel, String experienceYears, String amount, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.winnerEmail = winnerEmail;
        this.location = location;
        this.educationLevel = educationLevel;
        this.experienceYears = experienceYears;
        this.amount = amount;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters for each field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWinnerEmail() {
        return winnerEmail;
    }

    public void setWinnerEmail(String winnerEmail) {
        this.winnerEmail = winnerEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(String experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
