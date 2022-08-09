package com.example.universityfoodsystem;

public class Favorite {
    String restaurantName;
    String imageUrl;

    public Favorite() {
    }

    public Favorite(String restaurantName, String imageUrl) {
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
