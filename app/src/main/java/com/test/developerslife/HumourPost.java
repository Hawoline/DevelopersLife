package com.test.developerslife;

public class HumourPost {
    private String gif;
    private String description;

    public HumourPost(String gif, String description) {
        this.gif = gif;
        this.description = description;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
