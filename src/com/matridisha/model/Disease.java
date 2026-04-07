package com.matridisha.model;


// Disease Model Class
public class Disease {

    // Class Variables (Attributes)
    private String name;
    private String imageName;
    private String link;

    // Constructor
    public Disease(String name, String imageName, String link) {
        this.name = name;
        this.imageName = imageName;
        this.link = link;
    }

    // Getter Methods
    public String getName() { 
        return name; 
    }

    public String getImageName() { 
        return imageName; 
    }

    public String getLink() { 
        return link; 
    }
}