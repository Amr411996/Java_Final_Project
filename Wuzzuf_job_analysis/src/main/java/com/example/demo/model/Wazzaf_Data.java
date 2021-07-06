/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.model;

/**
 *
 * @author lenovo
 */
public class Wazzaf_Data {
    
    private String title;
    private String company;
    private String location;
    private String type;
    private String level;
    private String yearsexp;
    private String country;
    private String skills;

    public Wazzaf_Data(String title, String company, String location, String type, String level, String yearsexp, String country, String skills) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.type = type;
        this.level = level;
        this.yearsexp = yearsexp;
        this.country = country;
        this.skills = skills;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getYearsexp() {
        return yearsexp;
    }

    public void setYearsexp(String yearsexp) {
        this.yearsexp = yearsexp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    

   
    
    public String toString() {
        return "{" +
                "title=" + title +
                ", company=" + company +
                ", location=" + location + 
                ", type=" + type +
                 ", level=" + level +
                 ", yearsexp=" + yearsexp +
                 ", country=" + country +
                ", skills=" + skills +
                '}'+"\n";
    }
}
    

