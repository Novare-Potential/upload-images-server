package com.novare.uploadfile.dtos;

public class ResponseContentDto {
    private String title;
    private String imgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "ContentDto{" +
                "title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
