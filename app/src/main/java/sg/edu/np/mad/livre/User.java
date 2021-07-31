package sg.edu.np.mad.livre;

import java.util.ArrayList;

public class User {

    public String userID;
    public String email;
    public String name;
    public String photoURL;
    public ArrayList<Book> bookList;
    public ArrayList<Records> records;

    public User() {}

    public User(String userID, String email) {
        this.userID = userID;
        this.email = email;
    }
}
