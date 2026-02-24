package com.example.budgetapp.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetapp.R;
import com.example.budgetapp.data.local.AppDatabase;
import com.example.budgetapp.data.model.Category;
import com.example.budgetapp.data.model.Expense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    private AppDatabase db;

    private EditText editExpenseName, editExpenseAmount;

    private ArrayList<String> expenseList;
    private ExpenseAdapter expenseAdapter;
    private RecyclerView recyclerExpenses;

    private Spinner spinnerCategory;

    private Button buttonDate;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = AppDatabase.getInstance(this);

        super.onCreate(savedInstanceState);
        setupNavigation(R.layout.activity_main);

        editExpenseName = findViewById(R.id.editExpenseName);
        editExpenseAmount = findViewById(R.id.editExpenseAmount);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        buttonDate = findViewById(R.id.buttonDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        buttonAdd.setOnClickListener(view -> addExpense());

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense expenseToDelete = expenseAdapter.getItem(position);

                // Show confirmation dialog
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Expense")
                        .setMessage("Are you sure you want to delete this expense?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Delete from database
                            db.expenseDao().delete(expenseToDelete);
                            // Remove from adapter
                            expenseAdapter.removeItem(position);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Restore the swiped item visually
                            expenseAdapter.notifyItemChanged(position);
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            }
        };

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerExpenses);

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
        }

        expenseAdapter = new ExpenseAdapter(savedExpenses);

        recyclerExpenses.setAdapter(expenseAdapter);
        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));
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

        expenseAdapter.addItem(expense);

        expenseAdapter.notifyItemInserted(0);
        recyclerExpenses.scrollToPosition(0);

        expenseAdapter.notifyDataSetChanged();

        editExpenseName.setText("");
        editExpenseAmount.setText("");
    }

    @SuppressLint("DefaultLocale")
    private String formatExpenseList(Expense expense) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        return expense.name + " | $" +
                String.format("%.2f", expense.amount)
                + "\nDate: " + sdf.format(expense.date)
                + " | Category: " + expense.category.name();

    }
}
