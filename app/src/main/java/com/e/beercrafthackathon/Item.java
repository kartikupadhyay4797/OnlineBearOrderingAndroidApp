package com.e.beercrafthackathon;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {
    @SerializedName("abv")
    private String abv;
    @SerializedName("ibu")
    private String ibu;
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("style")
    private String style;
    @SerializedName("ounces")
    private float ounces;
    private int quantity;
    private String total_size;


    public String getTotal_size() {
        return total_size;
    }

    public void setTotal_size(String total_size) {
        this.total_size = total_size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Item() {
        quantity=1;
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public float getOunces() {
        return ounces;
    }

    public void setOunces(float ounces) {
        this.ounces = ounces;
    }
}
