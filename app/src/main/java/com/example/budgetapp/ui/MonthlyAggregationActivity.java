package com.example.budgetapp.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.budgetapp.R;
import com.example.budgetapp.data.local.AppDatabase;
import com.example.budgetapp.data.model.Category;
import com.example.budgetapp.data.model.CategoryTotal;
import com.example.budgetapp.data.model.Expense;
import com.example.budgetapp.data.model.MonthlyTotal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class MonthlyAggregationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigation(R.layout.activity_monthly_aggregation);

        AppDatabase db = AppDatabase.getInstance(this);

        BarChart barChart = findViewById(R.id.barChart);
        PieChart pieChart = findViewById(R.id.pieChart);
        Spinner spinner = findViewById(R.id.monthSpinner);

        CheckBox checkboxRent = findViewById(R.id.checkboxRent);

        Spinner categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);

        // Category Filter
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All");
        for (Category cat : Category.values()) {
            categories.add(cat.name());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilterSpinner.setAdapter(categoryAdapter);

        List<MonthlyTotal> monthlyTotals = db.expenseDao().getMonthlyTotals();

        ListView monthExpenseList = findViewById(R.id.monthExpenseList);
        ArrayList<String> monthExpenses = new ArrayList<>();

        ArrayAdapter<String> monthExpenseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, monthExpenses);

        monthExpenseList.setAdapter(monthExpenseAdapter);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Bar Chart Logic
        for (int i = monthlyTotals.size() - 1, counter = 0; i >= 0; i--, counter++) {
            entries.add(new BarEntry(counter, (float) monthlyTotals.get(i).total));

            labels.add(monthlyTotals.get(i).month);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Totals");
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);

        barChart.setVisibleXRangeMaximum(5);

        barChart.setDragEnabled(true);
        barChart.setScaleXEnabled(true);
        barChart.setPinchZoom(false);

        barChart.moveViewToX(labels.size());

        XAxis xAxis = barChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setDrawGridLines(false);

        barChart.invalidate();

        // Pie Chart Logic
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);

        spinner.setAdapter(adapter);

        spinner.setSelection(labels.size() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Pie chart
                String selectedMonth = labels.get(position);

                updatePieChart(selectedMonth, checkboxRent.isChecked(), db, pieChart);

                // Monthly Expense List
                String selectedCategory = categoryFilterSpinner.getSelectedItem().toString();
                loadExpensesForMonthAndCategory(selectedMonth, selectedCategory, db, monthExpenses, monthExpenseAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        categoryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);

                int monthPosition = spinner.getSelectedItemPosition();
                if (monthPosition >= 0 && monthPosition < labels.size()) {
                    String selectedMonth = labels.get(monthPosition);
                    loadExpensesForMonthAndCategory(selectedMonth, selectedCategory, db, monthExpenses, monthExpenseAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updatePieChart(
            String month,
            boolean showRent,
            AppDatabase db,
            PieChart pieChart) {
        List<CategoryTotal> categoryTotals = db.expenseDao().getCategoryTotals(month);

        List<PieEntry> pieEntries = new ArrayList<>();

        for (CategoryTotal ct : categoryTotals) {
            if (ct.total > 0) {
                if (!showRent && ct.category.equalsIgnoreCase("RENT")) continue;
                pieEntries.add(new PieEntry((float) ct.total, ct.category));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");

        ArrayList<Integer> colors = new ArrayList<>();
        for (PieEntry entry : pieEntries) {
            switch (entry.getLabel().toUpperCase()) {
                case "GROCERY":
                    colors.add(Color.parseColor("#fb7b77"));
                    break;
                case "DINING":
                    colors.add(Color.parseColor("#fdc170"));
                    break;
                case "RENT":
                    colors.add(Color.parseColor("#f3f87f"));
                    break;
                case "UTILITIES":
                    colors.add(Color.parseColor("#98f786"));
                    break;
                case "HOBBY":
                    colors.add(Color.parseColor("#69ebfc"));
                    break;
                case "ENTERTAINMENT":
                    colors.add(Color.parseColor("#6d9efc"));
                    break;
                case "TRANSPORTATION":
                    colors.add(Color.parseColor("#937df8"));
                    break;
                default:
                    colors.add(Color.parseColor("#f78ef0"));
                    break;
            }
        }
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }

    @SuppressLint("DefaultLocale")
    private String formatExpense(Expense expense) {

        @SuppressLint("SimpleDateFormat") java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd");

        return expense.name + " | $" + String.format("%.2f", expense.amount) + "\nDate: " + sdf.format(expense.date) + " | Category: " + expense.category.name();
    }

    private void loadExpensesForMonthAndCategory(
            String month,
            String categoryFilter,
            AppDatabase db,
            ArrayList<String> monthExpenses,
            ArrayAdapter<String> monthExpenseAdapter) {
        monthExpenses.clear();

        List<Expense> expensesForMonth = db.expenseDao().getExpensesForMonth(month);

        for (Expense expense : expensesForMonth) {
            // Filter by category if not "All"
            if (!categoryFilter.equals("All") && !expense.category.name().equalsIgnoreCase(categoryFilter)) {
                continue;
            }
            monthExpenses.add(formatExpense(expense));
        }

        monthExpenseAdapter.notifyDataSetChanged();
    }
}
