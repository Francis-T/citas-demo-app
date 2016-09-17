package org.citas.basicdemo;

/**
 * Created by francis on 9/13/16.
 */
public class Reading {
    private String source = "Unknown";
    private String type = "Unknown";
    private double value = 0.0;

    public Reading(String source, String type, double value) {
        this.source = source;
        this.type = type;
        this.value = value;
        return;
    }
    public String getSource() { return source; }
    public String getType() { return type; }
    public double getValue() { return value; }
}
