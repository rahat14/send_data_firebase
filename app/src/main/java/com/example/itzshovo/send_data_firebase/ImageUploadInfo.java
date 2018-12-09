package com.example.itzshovo.send_data_firebase;

public class ImageUploadInfo {
    String title ;
    String descrpition ;
    String image ;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String title, String descrpition, String image) {
        this.title = title;
        this.descrpition = descrpition;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescrpition() {
        return descrpition;
    }

    public String getImage() {
        return image;
    }
}
