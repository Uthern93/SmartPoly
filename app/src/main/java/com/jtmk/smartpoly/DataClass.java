package com.jtmk.smartpoly;

public class DataClass {
    private String imageURL;
    private String title;
    private String caption;
    private String Edate;
    private String Etime;
    private String uploadTime;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEdate() {
        return Edate;
    }

    public void setEdate(String edate) {
        Edate = edate;
    }

    public String getEtime() {
        return Etime;
    }

    public void setEtime(String etime) {
        Etime = etime;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }


    public DataClass() {

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }



    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public DataClass(String imageURL, String title, String caption, String edate, String etime, String uploadTime) {
        this.imageURL = imageURL;
        this.title = title;
        this.caption = caption;
        this.Edate = edate;
        this.Etime = etime;
        this.uploadTime = uploadTime;
    }

}
