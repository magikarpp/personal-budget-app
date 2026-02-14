package com.example.budgetapp.data.local;

import androidx.room.TypeConverter;

import com.example.budgetapp.data.model.Category;

import java.util.Date;

public class Converters {

    // Date → Long
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    // Long → Date
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    // Category → String
    @TypeConverter
    public static String fromCategory(Category category) {
        return category == null ? null : category.name();
    }

    // String → Category
    @TypeConverter
    public static Category toCategory(String category) {
        return category == null ? null : Category.valueOf(category);
    }
}
