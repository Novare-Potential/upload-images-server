package com.novare.uploadfile.dtos;

public class RequestContentDto {
    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "RequestContentDto{" +
                "title='" + title + '\'' +
                '}';
    }
}
