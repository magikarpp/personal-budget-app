package com.example.budgetapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double amount;
    public Date date;
    public Category category;

    public Expense(String name, double amount, Date date, Category category) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }
}

