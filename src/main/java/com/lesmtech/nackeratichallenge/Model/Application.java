package com.lesmtech.nackeratichallenge.Model;

import java.io.Serializable;

/**
 * Created by Te on 1/31/15.
 */
public class Application implements Serializable{

    String id;
    String name;
    String image;
    String summary;
    String price;
    String contenttype;
    String rights;
    String title;
    String artist;
    String category;
    String releaseDate;

    public Application(String id, String name, String image, String summary, String price, String contenttype, String rights, String title, String artist, String category, String releaseDate) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.summary = summary;
        this.price = price;
        this.contenttype = contenttype;
        this.rights = rights;
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
