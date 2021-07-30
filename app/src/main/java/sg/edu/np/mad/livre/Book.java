package sg.edu.np.mad.livre;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
    private int ID;
    private String isbn;
    private String name;
    private String author;
    private String blurb;
    private String year;
    private int readSeconds;
    private String thumbnail;
    private boolean isCustom;
    private boolean isArchived;
    private boolean isAdded;

    public boolean isArchived() {
        return isArchived;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getReadSeconds() {
        return readSeconds;
    }

    public void setReadSeconds(int readSeconds) {
        this.readSeconds = readSeconds;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ID=" + ID +
                ", isbn='" + isbn + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", blurb='" + blurb + '\'' +
                ", year='" + year + '\'' +
                ", readSeconds=" + readSeconds +
                ", isCustom=" + isCustom +
                ", isArchived=" + isArchived +
                ", isAdded=" + isAdded +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
