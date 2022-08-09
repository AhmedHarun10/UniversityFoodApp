package com.example.universityfoodsystem;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class PopularRestaurants {
    private String likedMeal;
    private String mealImage;
    private String mealName;
    private String mealPrice;
    private String mealRating;
    private String mealCalories;

    public PopularRestaurants() {
    }

    public PopularRestaurants(String likedMeal, String mealImage, String mealName, String mealPrice, String mealRating, String mealCalories) {
        this.likedMeal = likedMeal;
        this.mealImage = mealImage;
        this.mealName = mealName;
        this.mealPrice = mealPrice;
        this.mealRating = mealRating;
        this.mealCalories = mealCalories;
    }

    public String getLikedMeal() {
        return likedMeal;
    }

    public void setLikedMeal(String likedMeal) {
        this.likedMeal = likedMeal;
    }

    public String getMealImage() {
        return mealImage;
    }

    public void setMealImage(String mealImage) {
        this.mealImage = mealImage;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(String mealPrice) {
        this.mealPrice = mealPrice;
    }

    public String getMealRating() {
        return mealRating;
    }

    public void setMealRating(String mealRating) {
        this.mealRating = mealRating;
    }

    public String getMealCalories() {
        return mealCalories;
    }

    public void setMealCalories(String mealCalories) {
        this.mealCalories = mealCalories;
    }
}
