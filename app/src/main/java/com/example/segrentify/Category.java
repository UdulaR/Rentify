package com.example.segrentify;
import java.util.NoSuchElementException;

public class Category {

    private String id;
    private String name;
    private String description;


    public Category(){

    }
    public Category(String id,String name, String desc){
        this.id = id;
        this.name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
        description = desc;
    }

    public void update(String newName, String newDesc){
        name = newName;
        description = newDesc;
    }

    public void delete(){
        name = null;
        description = null;
    }


    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getId(){
        return id;
    }


}
