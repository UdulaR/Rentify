package com.example.segrentify;


public class Item {
     String id;
     String fee;
     String timePeriod;
     String item_name;
     String description;
     String category;
     String owner;


    public Item(){

    }
     public Item(String id, String fee, String timePeriod, String item_name, String description, String category,String owner) {
         this.id = id;
         this.fee = fee;
         this.timePeriod = timePeriod;
         this.item_name = item_name.substring(0, 1).toUpperCase() + item_name.substring(1).toLowerCase();
         this.description = description;
         this.category = category;
         this.owner = owner;
     }
     public void setId(String id){
         this.id = id;
     }
    public void setFee(String fee){
        this.fee = fee;
    }
    public void setTimePeriod(String timePeriod){
        this.timePeriod = timePeriod;
    }
    public void setItem_name(String name){
        this.item_name = name;
    }
    public void setDescription(String desc){
        this.description = desc;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public String getFee() {
        return fee;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
