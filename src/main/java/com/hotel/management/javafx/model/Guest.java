package com.hotel.management.javafx.model;

public class Guest {
 private String ssn;
 private String name;
 private String phoneNumber;
 private String email;
 
 public Guest(String ssn,String name,String phoneNumber, String email)
 {
     setSsn(ssn);
     setName(name);
     setPhoneNumber(phoneNumber);
     setEmail(email);
 }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        if(ssn== null || ssn.trim().isEmpty())
        {
            throw new IllegalArgumentException("SSN cannot be empty");
        }
        if(ssn.length()<14)
        {
            throw new IllegalArgumentException("Invalid SSN");
        }
        this.ssn = ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name==null || name.trim().isEmpty() )
        {
            throw new IllegalArgumentException("Enter a valid Name");
        }
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null ||phoneNumber.trim().isEmpty()|| phoneNumber.length() < 11) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email;
    }
    @Override
    public String toString() {
        return "Guest{" + "ssn='" + ssn + '\'' +", fullName='" + name + '\'' +", phoneNumber='" + phoneNumber + '\'' + ", email='" + email + '\'' +'}';  }
}
