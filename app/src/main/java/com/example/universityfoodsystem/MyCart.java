package com.example.universityfoodsystem;

import java.util.ArrayList;

public class MyCart {
    private static MyCart mInstance = null;

    ArrayList<Item> cart = new ArrayList<>();

    protected MyCart(){}

    public static synchronized MyCart getInstance() {
        if(mInstance == null){
            mInstance = new MyCart();
        }
        return mInstance;
    }

    public void addItems(String item, String price, String minteger){
        cart.add(new Item(item, price, String.valueOf(minteger)));
    }
    public ArrayList<Item> getCart(){
        return cart;
    }

    public static void reset(){
        mInstance = new MyCart();
    }
}
