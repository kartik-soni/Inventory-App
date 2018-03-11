package com.example.kartik.inventoryapp.data;

/**
 * Created by KARTIK on 3/11/2018.
 */

public class StockProvider {
    private final String mProductName;
    private final String mProductPrice;
    private final int mProductQuantity;
    private final String mSupplierName;
    private final String mSupplierPhone;
    private final String mImage;

    public StockProvider(String productName, String price, int quantity, String supplierName, String supplierPhone, String image) {
        mProductName = productName;
        mProductPrice = price;
        mProductQuantity = quantity;
        mSupplierName = supplierName;
        mSupplierPhone = supplierPhone;
        mImage = image;
    }

    public String getProductName() {
        return mProductName;
    }

    public String getProductPrice() {
        return mProductPrice;
    }

    public int getProductQuantity() {
        return mProductQuantity;
    }

    public String getSupplierName() {
        return mSupplierName;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public String getImage() {
        return mImage;
    }
   /* @Override
    public String toString() {
        return "StockProvider{" +
                "productName='" + productName + '\'' +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                ", supplierName='" + supplierName + '\'' +
                ", supplierPhone='" + supplierPhone + '\'' +
                ", supplierEmail='" + supplierEmail + '\'' +
                '}';
    }*/

}
