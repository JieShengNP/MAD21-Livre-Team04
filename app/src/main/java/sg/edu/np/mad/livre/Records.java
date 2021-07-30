package sg.edu.np.mad.livre;

import java.util.Date;

public class Records {
    private int _id;
    private String isbn;
    private Date dateRead;
    private int timeReadSec;
    private String name;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getDateRead() {
        return dateRead;
    }

    public void setDateRead(Date dateRead) {
        this.dateRead = dateRead;
    }

    public int getTimeReadSec() {
        return timeReadSec;
    }

    public void setTimeReadSec(int timeReadSec) {
        this.timeReadSec = timeReadSec;
    }

    public Records(){};
    public Records(String I, Date D, int T, String N)
    {
        this.setDateRead(D);
        this.setName(N);
        this.setTimeReadSec(T);
        this.setIsbn(I);
    }
}
