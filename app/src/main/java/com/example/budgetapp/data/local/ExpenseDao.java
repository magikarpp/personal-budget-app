package com.example.budgetapp.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.budgetapp.data.model.CategoryTotal;
import com.example.budgetapp.data.model.Expense;
import com.example.budgetapp.data.model.MonthlyTotal;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(Expense expense);

    @Query("SELECT * " +
            "FROM expenses " +
            "ORDER BY date DESC")
    List<Expense> getAllExpenses();

    @Query("DELETE FROM expenses")
    void deleteAll();

    @Query("SELECT strftime('%Y-%m', date/1000, 'unixepoch') AS month, " +
            "SUM(amount) as total " +
            "FROM expenses " +
            "GROUP BY month " +
            "ORDER BY date DESC")
    List<MonthlyTotal> getMonthlyTotals();

    @Query("SELECT * " +
            "FROM expenses " +
            "WHERE strftime('%Y-%m', date / 1000, 'unixepoch') = :month " +
            "ORDER BY date DESC")
    List<Expense> getExpensesForMonth(String month);

    @Query("SELECT category, " +
            "SUM(amount) as total " +
            "FROM expenses " +
            "WHERE strftime('%Y-%m', date/1000, 'unixepoch') = :month " +
            "GROUP BY category")
    List<CategoryTotal> getCategoryTotals(String month);

}
