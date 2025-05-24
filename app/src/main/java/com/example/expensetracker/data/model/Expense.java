package com.example.expensetracker.data.model;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses",foreignKeys = @ForeignKey(entity = Category.class,parentColumns = "id",childColumns = "categoryId",onDelete = ForeignKey.CASCADE))
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String merchant;
    public long date;
    public String source;
    public int categoryId;

    public Expense(double amount, String merchant, long date, String source, int categoryId) {
        this.amount = amount;
        this.merchant = merchant;
        this.date = date;
        this.source = source;
        this.categoryId= categoryId;
    }
}
