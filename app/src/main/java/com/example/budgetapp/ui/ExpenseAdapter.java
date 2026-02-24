package com.example.budgetapp.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetapp.R;
import com.example.budgetapp.data.model.Expense;

import java.text.SimpleDateFormat;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.textView.setText(formatExpense(expense));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void removeItem(int position) {
        expenses.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Expense expense) {
        expenses.add(0, expense);
        notifyItemInserted(0);
    }

    public Expense getItem(int position) {
        return expenses.get(position);
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatExpense(Expense expense) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        return expense.name + " | $" + String.format("%.2f", expense.amount) +
                "\nDate: " + sdf.format(expense.date) + " | Category: " + expense.category.name();
    }
}