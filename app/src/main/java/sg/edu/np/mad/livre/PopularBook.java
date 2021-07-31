package sg.edu.np.mad.livre;

import java.util.ArrayList;
import java.util.HashMap;

public class PopularBook {
    public String isbn;
    public String title;
    public String author;
    public String blurb;
    public String year;
    public String thumbnail;
    public HashMap<String, Boolean> readers;
    private int totalReaders;
    public int totalTime;

    public int getTotalReaders() {
        return totalReaders;
    }

    public void setTotalReaders() {
        this.totalReaders = readers.size();
    }

    public PopularBook(){
        readers = new HashMap<>();
    }

}
