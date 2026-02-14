package com.example.budgetapp.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.budgetapp.R;
import com.example.budgetapp.data.local.AppDatabase;
import com.example.budgetapp.data.model.Category;
import com.example.budgetapp.data.model.Expense;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    private AppDatabase db;

    private EditText editExpenseName, editExpenseAmount;

    private ArrayList<String> expenseList;
    private ArrayAdapter<String> expenseAdapter;

    private Spinner spinnerCategory;

    private Button buttonDate;
    private Date selectedDate;

    private double totalSpent = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = AppDatabase.getInstance(this);

        super.onCreate(savedInstanceState);
        setupNavigation(R.layout.activity_main);

        editExpenseName = findViewById(R.id.editExpenseName);
        editExpenseAmount = findViewById(R.id.editExpenseAmount);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        ListView listExpenses = findViewById(R.id.listExpenses);
        buttonDate = findViewById(R.id.buttonDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        buttonAdd.setOnClickListener(view -> addExpense());

        // Date Picker Logic
        selectedDate = new Date();

        buttonDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDate = calendar.getTime();
                        buttonDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Category Picker Logic
        ArrayAdapter<Category> categoryAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        Category.values());

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(categoryAdapter);

        // Expense List Logic
        expenseList = new ArrayList<>();

        List<Expense> savedExpenses = db.expenseDao().getAllExpenses();

        for (Expense expense : savedExpenses) {
            expenseList.add(formatExpenseList(expense));
            totalSpent += expense.amount;
        }

        expenseAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, expenseList);

        listExpenses.setAdapter(expenseAdapter);
    }

    private void addExpense() {
        String name = editExpenseName.getText().toString().trim();
        String amountText = editExpenseAmount.getText().toString().trim();

        if (name.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please enter name and amount",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory =
                (Category) spinnerCategory.getSelectedItem();

        Expense expense = new Expense(name, amount, selectedDate, selectedCategory);
        db.expenseDao().insert(expense);

        expenseList.add(0, formatExpenseList(expense));

        totalSpent += amount;

        expenseAdapter.notifyDataSetChanged();

        editExpenseName.setText("");
        editExpenseAmount.setText("");
    }

    private String formatExpenseList(Expense expense) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        return expense.name + " | $" +
                String.format("%.2f", expense.amount)
                + "\nDate: " + sdf.format(expense.date)
                + " | Category: " + expense.category.name();

    }
}
