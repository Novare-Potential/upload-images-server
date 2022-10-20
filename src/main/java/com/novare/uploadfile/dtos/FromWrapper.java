package com.novare.uploadfile.dtos;

import org.springframework.web.multipart.MultipartFile;

public class FromWrapper {
    private MultipartFile image;
    private String title;


    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
