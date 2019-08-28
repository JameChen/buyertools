package com.nahuo.buyertool.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GoodBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    public int                ShopID;
    public int                ItemID;
    public String             Name;
    public String             Intro;
    public String             Cover;
    public double             RetailPrice;
    public double             Price;
    public int                ApplyStatuID;

    private List<Product>     Products         = new ArrayList<Product>();

    private Set<String>       sizes            = new LinkedHashSet<String>();

    private Set<String>       colors           = new LinkedHashSet<String>();

    public GoodBaseInfo(int itenId, String name, String intro, String cover, double retailPrice, double price,
                        int applyStatuID) {
        this.ItemID = itenId;
        this.Name = name;
        this.Intro = intro;
        this.Cover = cover;
        this.RetailPrice = retailPrice;
        this.Price = price;
        this.ApplyStatuID = applyStatuID;
    }

    public String[] getSizes() {
        if (Products != null && !Products.isEmpty()) {
            sizes = new LinkedHashSet<String>();
            for (Product product : Products) {
                // Log.i(getClass().getSimpleName(), "size:" + product.Size);
                sizes.add(product.Size);
            }
        }
        String[] array = (String[])sizes.toArray(new String[sizes.size()]);
        return array;
    }

    public void addSize(String size) {
        sizes.add(size);
    }

    public String[] getColors() {
        if (Products != null && !Products.isEmpty()) {
            colors = new LinkedHashSet<String>();
            for (Product product : Products) {
                colors.add(product.Color);
            }
        }
        String[] array = (String[])colors.toArray(new String[colors.size()]);
        return array;
    }

    public void addColor(String color) {
        colors.add(color);
    }
}
