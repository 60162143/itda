package com.example.itda.ui.info;

import java.io.File;

public class infoReviewPhotoFileData {
    private File file;      // 파일 내용
    private int reviewId;   // 리뷰 고유 아이디
    private String fileName;    // 파일 명
    private String fileEts;     // 파일 확장자
    private double fileSize;    // 파일 크기

    public infoReviewPhotoFileData(File file, int reviewId, String fileName, String fileEts, double fileSize) {
        this.file = file;
        this.reviewId = reviewId;
        this.fileName = fileName;
        this.fileEts = fileEts;
        this.fileSize = fileSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileEts() {
        return fileEts;
    }

    public void setFileEts(String fileEts) {
        this.fileEts = fileEts;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }
}
