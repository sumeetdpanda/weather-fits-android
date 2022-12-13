package com.conestoga.weatherfits.model;

public class Product {
    private String Name;
    private String Link;
    private String Image;

    public Product(String Name, String Link, String Image){
        this.Name = Name;
        this.Link = Link;
        this.Image = Image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
