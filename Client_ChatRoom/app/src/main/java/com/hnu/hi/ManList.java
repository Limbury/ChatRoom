package com.hnu.hi;

public class ManList {
    private Integer id;
    private String name;
    private int imageId;
    public ManList(Integer id,String name,int imageId){
        this.name = name;
        this.imageId = imageId;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public Integer getId(){return id;}

    public String getIdName(){
        return id+"("+name+")";
    }

    public int getImageId(){
        return imageId;
    }
}
