package com.hnu.hi;

public class ManList {
    private String name;
    private int imageId;
    public ManList(String name,int imageId){
        this.name = name;
        this.imageId = imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }
}
