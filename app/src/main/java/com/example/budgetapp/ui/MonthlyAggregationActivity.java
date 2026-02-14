package com.example.budgetapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.budgetapp.R;
import com.example.budgetapp.data.local.AppDatabase;
import com.example.budgetapp.data.model.CategoryTotal;
import com.example.budgetapp.data.model.MonthlyTotal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationView;

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

        List<MonthlyTotal> monthlyTotals = db.expenseDao().getMonthlyTotals(6);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Bar Chart Logic
        for (int i = 0; i < monthlyTotals.size(); i++) {
            entries.add(new BarEntry(i, (float) monthlyTotals.get(i).total));

            labels.add(monthlyTotals.get(i).month);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Totals");
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.invalidate();

        // Pie Chart Logic
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);

        spinner.setAdapter(adapter);

        spinner.setSelection(labels.size() - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedMonth = labels.get(position);

                List<CategoryTotal> categoryTotals = db.expenseDao().getCategoryTotals(selectedMonth);

                List<PieEntry> pieEntries = new ArrayList<>();

                for (CategoryTotal ct : categoryTotals) {
                    if (ct.total > 0) {
                        pieEntries.add(new PieEntry((float) ct.total, ct.category));
                    }
                }

                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
