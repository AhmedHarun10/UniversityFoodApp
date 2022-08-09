package com.example.universityfoodsystem.restaurantInfo;

public class restaurantHelperClass {
    public restaurantHelperClass() {
    }

    String restaurantName, restaurantAddress, restaurantState, restaurantZipcode, restaurantPhone, restaurantGenre;

    public restaurantHelperClass(String restaurantName, String restaurantAddress, String restaurantState, String restaurantZipcode, String restaurantPhone, String restaurantGenre) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantState = restaurantState;
        this.restaurantZipcode = restaurantZipcode;
        this.restaurantPhone = restaurantPhone;
        this.restaurantGenre = restaurantGenre;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantState() {
        return restaurantState;
    }

    public void setRestaurantState(String restaurantState) {
        this.restaurantState = restaurantState;
    }

    public String getRestaurantZipcode() {
        return restaurantZipcode;
    }

    public void setRestaurantZipcode(String restaurantZipcode) {
        this.restaurantZipcode = restaurantZipcode;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getRestaurantGenre() {
        return restaurantGenre;
    }

    public void setRestaurantGenre(String restaurantGenre) {
        this.restaurantGenre = restaurantGenre;
    }
}