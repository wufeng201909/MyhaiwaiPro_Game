package com.sdk.mysdklibrary.localbeans;

public class MyProductDetails {
    private String productId;       //商品ID
    private String formattedPrice;       //格式化价格，带货币单位

    public MyProductDetails(String productId, String formattedPrice){
        this.productId = productId;
        this.formattedPrice = formattedPrice;
    }
    public String getProductId() {
        return productId;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

}
