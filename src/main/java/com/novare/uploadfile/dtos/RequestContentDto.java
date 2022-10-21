package com.novare.uploadfile.dtos;

public class RequestContentDto {
    private String title;
    private String image;

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "RequestContentDto{" +
                "title='" + title + '\'' +
                '}';
    }
}
