package com.example.capstone1.v2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Locale;

@IgnoreExtraProperties
public class Notification {

    String from;
    String to;
    String message;
    String operation;
    String medication;
    String date;
    String time;
    String dosage;
    String endDate;
    int frequency;
    String frequencyName;
    int hour;
    String startDate;
    String medicineTypeName;



    Boolean read;
    public Notification() {


    }
    public Notification(String from, String to, String operation, String medication, String date, String time, String dosage, String endDate, int frequency, String frequencyName, int hour, String startDate, String medicineTypeName) {

        this.from = from;
        this.to = to;
        this.operation = operation;
        this.medication = medication;
        this.date = date;
        this.time = time;
        this.dosage = dosage;
        this.endDate = endDate;
        this.frequency = frequency;
        this.frequencyName = frequencyName;
        this.hour = hour;
        this.startDate = startDate;
        this.medicineTypeName = medicineTypeName;
        this.read = false;
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyName() {
        return frequencyName;
    }

    public void setFrequencyName(String frequencyName) {
        this.frequencyName = frequencyName;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMedicineTypeName() {
        return medicineTypeName;
    }

    public void setMedicineTypeName(String medicineTypeName) {
        this.medicineTypeName = medicineTypeName;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
    public String buildMessage() {
        return "Your dependent " + this.operation.toLowerCase() + " " + "your medicine, " + this.medication +  " " +  this.medicineTypeName.toLowerCase() + " to be taken " + this.frequencyName.toLowerCase()
        + " every " + Integer.toString(this.hour) + " hour, starting from " + this.startDate + " to " + this.endDate;
    }

}
