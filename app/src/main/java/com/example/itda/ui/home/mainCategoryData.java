package com.example.itda.ui.home;

public class mainCategoryData {
    private int categoryId;     // 카테고리 고유 아이디
    private String categoryNm;  // 카테고리 명
    private String imagePath;   // 카테고리 이미지 경로

    // Constructor
    public mainCategoryData(int categoryId, String categoryNm, String imagePath) {
        this.categoryId = categoryId;
        this.categoryNm = categoryNm;
        this.imagePath = imagePath;
    }

    // Getter, Setter
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryNm() {
        return categoryNm;
    }

    public void setCategoryNm(String categoryNm) {
        this.categoryNm = categoryNm;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
