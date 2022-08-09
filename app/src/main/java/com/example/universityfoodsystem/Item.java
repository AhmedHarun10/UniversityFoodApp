package com.example.universityfoodsystem;

public class Item {
    private String name;
    private String price;
    private String quantity;

    public Item(String text1, String text2, String text3) {
        name = text1;
        price = text2;
        quantity = text3;
    }

    public String getText1() {
        return name;
    }

    public String getText2() {
        return price;
    }
    public String getText3() {
        return quantity;
    }

}
