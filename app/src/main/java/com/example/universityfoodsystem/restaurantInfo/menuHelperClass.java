package com.example.universityfoodsystem.restaurantInfo;

public class menuHelperClass {
    String menuItemName, menuItemPrice;

    public menuHelperClass() {
    }

    public menuHelperClass(String menuItemName, String menuItemPrice) {
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemPrice() {
        return menuItemPrice;
    }

    public void setMenuItemPrice(String menuItemPrice) {
        this.menuItemPrice = menuItemPrice;
    }
}

