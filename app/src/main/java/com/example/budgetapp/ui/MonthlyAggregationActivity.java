package com.example.budgetapp.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.budgetapp.R;
import com.example.budgetapp.data.local.AppDatabase;
import com.example.budgetapp.data.model.CategoryTotal;
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

        List<MonthlyTotal> monthlyTotals = db.expenseDao().getMonthlyTotals();

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Bar Chart Logic
        for (int i = monthlyTotals.size() - 1, counter = 0; i >= 0; i--, counter++) {
            System.out.println(monthlyTotals.get(i).month);
            System.out.println(monthlyTotals.get(i).total);

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

                String selectedMonth = labels.get(position);

                List<CategoryTotal> categoryTotals = db.expenseDao().getCategoryTotals(selectedMonth);

                List<PieEntry> pieEntries = new ArrayList<>();

                for (CategoryTotal ct : categoryTotals) {
                    if (ct.total > 0) {
                        pieEntries.add(new PieEntry((float) ct.total, ct.category));
                    }
                }

//                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
//                PieData pieData = new PieData(pieDataSet);
//
//                pieChart.setData(pieData);
//                pieChart.invalidate();

                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");

                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#2196F3"));
                colors.add(Color.parseColor("#4CAF50"));
                colors.add(Color.parseColor("#FF9800"));
                colors.add(Color.parseColor("#9C27B0"));

                pieDataSet.setColors(colors);

                pieDataSet.setSliceSpace(3f);
                pieDataSet.setSelectionShift(5f);

                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);

                pieChart.getDescription().setEnabled(false);
                pieChart.setUsePercentValues(false);

                pieChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
