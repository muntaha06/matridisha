package com.matridisha.model;

public class Disease {
    private String name;
    private String imageName;
    private String link;

    public Disease(String name, String imageName, String link) {
        this.name = name;
        this.imageName = imageName;
        this.link = link;
    }

    public String getName() { return name; }
    public String getImageName() { return imageName; }
    public String getLink() { return link; }
}