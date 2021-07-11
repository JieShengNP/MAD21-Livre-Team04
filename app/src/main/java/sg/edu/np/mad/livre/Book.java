package sg.edu.np.mad.livre;

import java.util.ArrayList;

public class Book {
    public String isbn;
    public String name;
    public String author;
    public String blurb;
    public String thumbnail;
    public int readSeconds;
    public boolean isCustom;

    public ArrayList<Book> bookArrayList;

    public ArrayList<Book> getBookArrayList() {
        return bookArrayList;
    }

    public void setBookArrayList(ArrayList<Book> bookArrayList) {
        this.bookArrayList = bookArrayList;
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

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getReadSeconds() {
        return readSeconds;
    }

    public void setReadSeconds(int readSeconds) {
        this.readSeconds = readSeconds;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn=" + isbn +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", blurb='" + blurb + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", readSeconds=" + readSeconds +
                ", isCustom=" + isCustom +
                '}';
    }
}
