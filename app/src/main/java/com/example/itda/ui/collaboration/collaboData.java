package com.example.itda.ui.collaboration;

public class collaboData {
    private int CollaboId;
    private int FrontStoreId;
    private int BackStoreId;
    private String FrontStoreName;
    private String BackStoreName;
    private String FrontStoreThumbnail;
    private String BackStoreThumbnail;
    private String DiscountConditional;
    private String Discount;

    public collaboData(int collaboId, int frontStoreId, int backStoreId, String frontStoreName, String backStoreName, String frontStoreThumbnail, String backStoreThumbnail, String discountConditional, String discount) {
        CollaboId = collaboId;
        FrontStoreId = frontStoreId;
        BackStoreId = backStoreId;
        FrontStoreName = frontStoreName;
        BackStoreName = backStoreName;
        FrontStoreThumbnail = frontStoreThumbnail;
        BackStoreThumbnail = backStoreThumbnail;
        DiscountConditional = discountConditional;
        Discount = discount;
    }

    public int getCollaboId() {
        return CollaboId;
    }

    public void setCollaboId(int collaboId) {
        CollaboId = collaboId;
    }

    public int getFrontStoreId() {
        return FrontStoreId;
    }

    public void setFrontStoreId(int frontStoreId) {
        FrontStoreId = frontStoreId;
    }

    public int getBackStoreId() {
        return BackStoreId;
    }

    public void setBackStoreId(int backStoreId) {
        BackStoreId = backStoreId;
    }

    public String getFrontStoreName() {
        return FrontStoreName;
    }

    public void setFrontStoreName(String frontStoreName) {
        FrontStoreName = frontStoreName;
    }

    public String getBackStoreName() {
        return BackStoreName;
    }

    public void setBackStoreName(String backStoreName) {
        BackStoreName = backStoreName;
    }

    public String getFrontStoreThumbnail() {
        return FrontStoreThumbnail;
    }

    public void setFrontStoreThumbnail(String frontStoreThumbnail) {
        FrontStoreThumbnail = frontStoreThumbnail;
    }

    public String getBackStoreThumbnail() {
        return BackStoreThumbnail;
    }

    public void setBackStoreThumbnail(String backStoreThumbnail) {
        BackStoreThumbnail = backStoreThumbnail;
    }

    public String getDiscountConditional() {
        return DiscountConditional;
    }

    public void setDiscountConditional(String discountConditional) {
        DiscountConditional = discountConditional;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
