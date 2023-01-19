package com.example.olapark.nav.payments;

public class Payment {

    private String number;
    private String date;
    private String park;
    private String value;

    public Payment(String number, String date, String park, String value){
        this.number = number;
        this.date = date;
        this.park = park;
        this.value = value;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
