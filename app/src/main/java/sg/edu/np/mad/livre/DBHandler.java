package sg.edu.np.mad.livre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Livre.db";
    public static final String TABLE_BOOK = "Book";
    public static final String BOOK_COLUMN_ID = "_id";
    public static final String BOOK_COLUMN_ISBN = "ISBN";
    public static final String BOOK_COLUMN_AUTHOR = "AUTHOR";
    public static final String BOOK_COLUMN_YEAR = "YEAR";
    public static final String BOOK_COLUMN_TITLE = "TITLE";
    public static final String BOOK_COLUMN_BLURB = "BLURB";
    public static final String BOOK_COLUMN_THUMBNAIL = "THUMBNAIL";
    public static final String BOOK_COLUMN_READING_TIME = "READING_TIME";
    public static final String BOOK_COLUMN_CUSTOM = "CUSTOM";
    public static final String BOOK_COLUMN_ADDED = "ADDED";
    public static final String BOOK_COLUMN_ARCHIVED = "ARCHIVED";
    public static final String TABLE_LOG = "Log";
    public static final String LOG_COLUMN_ID = "_id";
    public static final String LOG_COLUMN_NAME = "Name";
    public static final String LOG_COLUMN_ISBN = "Isbn";
    public static final String LOG_COLUMN_DATE = "Date";
    public static final String LOG_COLUMN_SECOND = "Time";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOK + "(" + BOOK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BOOK_COLUMN_ISBN + " TEXT," + BOOK_COLUMN_AUTHOR + " TEXT," + BOOK_COLUMN_YEAR + " TEXT," + BOOK_COLUMN_TITLE + " TEXT," + BOOK_COLUMN_BLURB + " TEXT," + BOOK_COLUMN_THUMBNAIL + " TEXT," + BOOK_COLUMN_READING_TIME + " INT," + BOOK_COLUMN_CUSTOM + " INT," + BOOK_COLUMN_ADDED + " INT," + BOOK_COLUMN_ARCHIVED + " INT" + ")";
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_LOG + "(" + LOG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LOG_COLUMN_NAME+ " TEXT," + LOG_COLUMN_ISBN+ " INT," + LOG_COLUMN_DATE + " TEXT," + LOG_COLUMN_SECOND + " INT)";
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_LOG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        onCreate(db);
    }

    /**
     * Adding of Book into the Database.
     * @param book Book to be added
     */
    public void AddBook(Book book){
        ContentValues values = new ContentValues();
        values.put(BOOK_COLUMN_ISBN, book.getIsbn());
        values.put(BOOK_COLUMN_AUTHOR, book.getAuthor());
        values.put(BOOK_COLUMN_TITLE, book.getName());
        values.put(BOOK_COLUMN_YEAR, book.getYear());
        values.put(BOOK_COLUMN_BLURB, book.getBlurb());
        values.put(BOOK_COLUMN_THUMBNAIL, book.getThumbnail());
        values.put(BOOK_COLUMN_READING_TIME, book.getReadSeconds());
        values.put(BOOK_COLUMN_CUSTOM, book.isCustom()? 1: 0);
        values.put(BOOK_COLUMN_ARCHIVED, book.isArchived()? 1: 0);
        values.put(BOOK_COLUMN_ADDED, book.isAdded()? 1: 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_BOOK, null, values);
        db.close();
    }

    /**
     * Retriving of Book by its ISBN number.
     * @param ISBN ISBN of the book
     * @param isCus is book custom
     * @return Book if it exists in the Database.
     */
    public Book FindBookByISBN(String ISBN, Boolean isCus){
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + ISBN +"\" and " + BOOK_COLUMN_CUSTOM + " = " + (isCus ? 1 : 0);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        Book book = new Book();
        if (cursor.moveToFirst()){
            book.setIsbn(cursor.getString(1));
            book.setAuthor(cursor.getString(2));
            book.setYear(cursor.getString(3));
            book.setName(cursor.getString(4));
            book.setBlurb(cursor.getString(5));
            book.setThumbnail(cursor.getString(6));
            book.setReadSeconds(cursor.getInt(7));
            book.setCustom(cursor.getInt(8) == 1? true: false);
            book.setAdded(cursor.getInt(9) == 1? true: false);
            book.setArchived(cursor.getInt(10) == 1? true: false);
        } else {
            book = null;
        }
        cursor.close();
        return book;
    }


    /**
     * Retrieving of Book by its ISBN number.
     * @param id ISBN of the book
     * @return Book if it exists in the Database.
     */
    public Book FindBookByID(int id){
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ID + " = \"" + String.valueOf(id) +"\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        Book book = new Book();
        if (cursor.moveToFirst()){
            book.setIsbn(cursor.getString(1));
            book.setAuthor(cursor.getString(2));
            book.setYear(cursor.getString(3));
            book.setName(cursor.getString(4));
            book.setBlurb(cursor.getString(5));
            book.setThumbnail(cursor.getString(6));
            book.setReadSeconds(cursor.getInt(7));
            book.setCustom(cursor.getInt(8) == 1);
            book.setAdded(cursor.getInt(9) == 1);
            book.setArchived(cursor.getInt(10) == 1);
        } else {
            book = null;
        }
        return book;
    }

    /**
     * retrieving of Book by its ISBN number.
     * @param book to find id of book
     * @return id if it exists in the Database.
     *          "not found" if it does not.
     */
    public int GetBookId(Book book){
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + book.getIsbn() +"\" and " + BOOK_COLUMN_CUSTOM + " = " + (book.isCustom() ? 1 : 0)+"\"";
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();;
        db.close();
        return id;
    }

    /**
     * Retriving of Book by its ISBN number.
     * @param book updated version
     * @param id id of book to update
     * @return Book if it exists in the Database.
     */
    public int UpdateBook(Book book, String id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BOOK_COLUMN_ISBN, book.getIsbn());
        values.put(BOOK_COLUMN_AUTHOR, book.getAuthor());
        values.put(BOOK_COLUMN_TITLE, book.getName());
        values.put(BOOK_COLUMN_YEAR, book.getYear());
        values.put(BOOK_COLUMN_BLURB, book.getBlurb());
        values.put(BOOK_COLUMN_THUMBNAIL, book.getThumbnail());
        values.put(BOOK_COLUMN_READING_TIME, book.getReadSeconds());
        values.put(BOOK_COLUMN_CUSTOM, book.isCustom()? 1: 0);
        values.put(BOOK_COLUMN_ARCHIVED, book.isArchived()? 1: 0);
        values.put(BOOK_COLUMN_ADDED, book.isAdded()? 1: 0);

        int rowsAffected = db.update(TABLE_BOOK, values, "(" +BOOK_COLUMN_ID + " = " + id + ") and (" + BOOK_COLUMN_CUSTOM + " = " + (book.isCustom() ? 1 : 0) + ")", null);
        db.close();

        return rowsAffected;
    }


    /**
     * Toggling of book archival status in the Database.
     * @param book The archival status of book to be toggled
     */
    public void ToggleArchive(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        //Alternative Method
//        String dbQuery = "UPDATE " + TABLE_BOOK + " SET " + BOOK_COLUMN_ARCHIVED + " = " + (book.isArchived? 1: 0) + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + book.getIsbn() + "\"";
//        db.execSQL(dbQuery);
        ContentValues values = new ContentValues();
        values.put(BOOK_COLUMN_ARCHIVED, book.isArchived()? 1: 0);
        int rowsAffected = db.update(TABLE_BOOK, values, BOOK_COLUMN_ISBN + " = " + book.getIsbn(), null);
        db.close();
    }

    /**
     * Search for book with a string query
     * @param query to use to look for book
     * @return arraylist of books
     */
    public ArrayList<Book> searchCustomBookQuery(String query) {
        ArrayList<Book> bookList = new ArrayList<>();
        ArrayList<String> qList = new ArrayList<>(Arrays.asList(query.split(" ")));

        //run through a list of words in the query and add all unique books to booklist

        for (int i = 0; qList.size() > i; i++) {
            String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE ((" + BOOK_COLUMN_TITLE + " LIKE '%" + qList.get(i) + "%') or (" + BOOK_COLUMN_AUTHOR + " LIKE '%" + qList.get(i) + "%') or (" + BOOK_COLUMN_BLURB + " LIKE '%" + qList.get(i) + "%')) and " + BOOK_COLUMN_CUSTOM + " = 1";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(dbQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Book book = new Book();
                    book.setID(cursor.getInt(0));
                    book.setIsbn(cursor.getString(1));
                    book.setAuthor(cursor.getString(2));
                    book.setYear(cursor.getString(3));
                    book.setName(cursor.getString(4));
                    book.setBlurb(cursor.getString(5));
                    book.setThumbnail(cursor.getString(6));
                    book.setReadSeconds(cursor.getInt(7));
                    book.setCustom(cursor.getInt(8) == 1);
                    book.setAdded(cursor.getInt(9) == 1);
                    book.setArchived(cursor.getInt(10) == 1);

                    Boolean con = false;
                    if(bookList == null){
                        bookList.add(book);
                    }
                    else {
                        for (int j = 0; bookList.size() > j; j++) {
                            if (bookList.get(j).getIsbn().equals(book.getIsbn())) {
                                con = true;
                            }
                        }
                        if(!con){
                            bookList.add(book);
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.close();
        }
        return bookList;
    }

    /**
     * Remove of book from the Database.
     * @param book The book to be deleted
     * @return result
     */
    public boolean RemoveBook(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = false;
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + book.getIsbn() +"\" and " + BOOK_COLUMN_CUSTOM + " = " + (book.isCustom() ? 1 : 0);
        Cursor cursor = db.rawQuery(dbQuery, null);
        Book object = new Book();
        if (cursor.moveToFirst()){
            object.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_BOOK, BOOK_COLUMN_ID + " = ?", new String[] { String.valueOf(object.getID()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    /**
     * Remove of Logs of Book from the Database.
     * @param book The book to erase logs for
     */
    public void EraseLogs(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        String dbQuery = "DELETE FROM " + TABLE_LOG + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + book.getIsbn() +"\" and " + BOOK_COLUMN_CUSTOM + " = " + (book.isCustom() ? 1 : 0);
        db.execSQL(dbQuery);
        db.close();
    }


    /**
     * Gets all the books that are currently stored in the Database
     * @return An ArrayList that contains all the Books.
     */
    public ArrayList<Book> GetAllBooks(){
        ArrayList<Book> bookList = new ArrayList<>();
        String dbQuery = "SELECT * FROM " + TABLE_BOOK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst()){
            do {
                Book book = new Book();
                book.setID(cursor.getInt(0));
                book.setIsbn(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setYear(cursor.getString(3));
                book.setName(cursor.getString(4));
                book.setBlurb(cursor.getString(5));
                book.setThumbnail(cursor.getString(6));
                book.setReadSeconds(cursor.getInt(7));
                book.setCustom(cursor.getInt(8) == 1? true: false);
                book.setAdded(cursor.getInt(9) == 1? true: false);
                book.setArchived(cursor.getInt(10) == 1? true: false);
                bookList.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            bookList = null;
        }
        db.close();
        return bookList;
    }

    /*
        Creates a log entry of time read .
    */
    public void updateLog(int isbn,int seconds, String name)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(LOG_COLUMN_ISBN, isbn);
        values.put(LOG_COLUMN_DATE, formatter.format(Calendar.getInstance().getTime()));
        values.put(LOG_COLUMN_SECOND, seconds);
        values.put(LOG_COLUMN_NAME, name);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_LOG, null, values);
        db.close();
    }
    /*
        Updates total time read in Book table
    */
    public void updateTotalTime(Book book)
    {
        ContentValues values = new ContentValues();
        values.put(BOOK_COLUMN_READING_TIME, book.getReadSeconds());

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_BOOK, values, BOOK_COLUMN_ISBN + " = ?",new String[]{book.getIsbn()});
        db.close();
    }

    /**
     * Gets all the books that are currently stored in the Database
     * @return An ArrayList that contains all the Books.
     */
    public ArrayList<Book> GetAllArchivedBooks(){
        ArrayList<Book> bookList = new ArrayList<>();
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ARCHIVED + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, new String[] {String.valueOf(1)});
        if (cursor.moveToFirst()){
            do {
                Book book = new Book();
                book.setID(cursor.getInt(0));
                book.setIsbn(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setYear(cursor.getString(3));
                book.setName(cursor.getString(4));
                book.setBlurb(cursor.getString(5));
                book.setThumbnail(cursor.getString(6));
                book.setReadSeconds(cursor.getInt(7));
                book.setCustom(cursor.getInt(8) == 1? true: false);
                book.setAdded(cursor.getInt(9) == 1? true: false);
                book.setArchived(cursor.getInt(10) == 1? true: false);
                bookList.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            bookList = null;
        }
        db.close();
        return bookList;
    }

    /**
     * Gets all the books that are currently stored in the Database
     * @return An ArrayList that contains all the Books.
     */
    public ArrayList<Book> GetAllNonArchivedBooks(){
        ArrayList<Book> bookList = new ArrayList<>();
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ARCHIVED + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, new String[] {String.valueOf(0)});
        if (cursor.moveToFirst()){
            do {
                Book book = new Book();
                book.setID(cursor.getInt(0));
                book.setIsbn(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setYear(cursor.getString(3));
                book.setName(cursor.getString(4));
                book.setBlurb(cursor.getString(5));
                book.setThumbnail(cursor.getString(6));
                book.setReadSeconds(cursor.getInt(7));
                book.setCustom(cursor.getInt(8) == 1? true: false);
                book.setAdded(cursor.getInt(9) == 1? true: false);
                book.setArchived(cursor.getInt(10) == 1? true: false);
                bookList.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            bookList = null;
        }
        db.close();
        return bookList;
    }


    public int GetTotalReadingTimeInSec()
    {
        int totalTime = 0;
        String dbQuery = "SELECT SUM(" + LOG_COLUMN_SECOND + ") FROM " + TABLE_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            totalTime = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return  totalTime;
    }

    public ArrayList<Records> GetAllRecords(){
        ArrayList<Records> recordsList = new ArrayList<Records>();
        String dbQuery = "SELECT * FROM " + TABLE_LOG + " ORDER BY " + LOG_COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            do {
                Records records = new Records();
                records.setName(cursor.getString(1));
                records.setBookID(cursor.getInt(2));
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    records.setDateRead(sdf.parse(cursor.getString(3)));
                }catch (ParseException ex)
                {
                    records.setDateRead(null);
                }
                records.setTimeReadSec(cursor.getInt(4));
                recordsList.add(records);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }


    public boolean isBookAdded(Book book){
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE (" + BOOK_COLUMN_ISBN + " = " + book.getIsbn() + ") and (" + BOOK_COLUMN_CUSTOM + " = " +  (book.isCustom() ? 1 : 0) + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        else{
            cursor.close();
            db.close();
            return false;
        }
    }

    //Get Total Number of Books in Library
    public int GetTotalBooksInLibrary()
    {
        String dbQuery = "SELECT COUNT(*) FROM " + TABLE_BOOK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            return count;
        }
        cursor.close();
        db.close();
        return 0;
    }

    //Get Total Number of Books Read
    public int GetTotalBooksRead()
    {
        String dbQuery = "SELECT COUNT(*) FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_READING_TIME + " > 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            return count;
        }
        cursor.close();
        db.close();
        return 0;
    }

    public int GetAvgTimePerSession()
    {
        String dbQuery = "SELECT AVG(" + LOG_COLUMN_SECOND +") FROM " + TABLE_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            int count = cursor.getInt(0);
            cursor.close();
            db.close();
            return count;
        }
        cursor.close();
        db.close();
        return 0;
    }

    public Book GetBookMostTimeSpent()
    {
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " ORDER BY " + BOOK_COLUMN_READING_TIME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            if (cursor.getInt(7) == 0)
            {
                cursor.close();
                db.close();
                return null;
            }
            else
            {
                Book book = new Book();
                book.setID(cursor.getInt(0));
                book.setIsbn(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setYear(cursor.getString(3));
                book.setName(cursor.getString(4));
                book.setBlurb(cursor.getString(5));
                book.setThumbnail(cursor.getString(6));
                book.setReadSeconds(cursor.getInt(7));
                book.setCustom(cursor.getInt(8) == 1? true: false);
                book.setAdded(cursor.getInt(9) == 1? true: false);
                book.setArchived(cursor.getInt(10) == 1? true: false);

                cursor.close();
                db.close();
                return book;
            }
        }
        cursor.close();
        db.close();
        return null;
    }

    public String GetFavouriteAuthor()
    {
        String dbQuery = "SELECT " +BOOK_COLUMN_AUTHOR+" ,COUNT(*) FROM " + TABLE_BOOK + " GROUP BY "
                + BOOK_COLUMN_AUTHOR + " ORDER BY COUNT(*) DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        if(cursor.moveToFirst())
        {
            String bookAuthor = cursor.getString(0);
            cursor.close();
            db.close();
            return bookAuthor;
        }
        cursor.close();
        db.close();
        return "None";
    }

    public void AddFirebaseBookToDB(List<Book> bookList){
        SQLiteDatabase db = this.getWritableDatabase();
        for(Book book : bookList) {
            ContentValues values = new ContentValues();
            values.put(BOOK_COLUMN_ID, book.getID());
            values.put(BOOK_COLUMN_ISBN, book.getIsbn());
            values.put(BOOK_COLUMN_AUTHOR, book.getAuthor());
            values.put(BOOK_COLUMN_TITLE, book.getName());
            values.put(BOOK_COLUMN_YEAR, book.getYear());
            values.put(BOOK_COLUMN_BLURB, book.getBlurb());
            values.put(BOOK_COLUMN_THUMBNAIL, book.getThumbnail());
            values.put(BOOK_COLUMN_READING_TIME, book.getReadSeconds());
            values.put(BOOK_COLUMN_CUSTOM, book.isCustom() ? 1 : 0);
            values.put(BOOK_COLUMN_ARCHIVED, book.isArchived() ? 1 : 0);
            values.put(BOOK_COLUMN_ADDED, book.isAdded() ? 1 : 0);

            db.insert(TABLE_BOOK, null, values);
        }
        db.close();
    }

    public void AddFirebaseRecordToDB(List<Records> recordsList){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        for (Records record : recordsList) {
            ContentValues values = new ContentValues();
            values.put(LOG_COLUMN_ID, record.get_id());
            values.put(LOG_COLUMN_NAME, record.getName());
            values.put(LOG_COLUMN_ISBN, record.getBookID());
            values.put(LOG_COLUMN_DATE, formatter.format(Calendar.getInstance().getTime()));
            values.put(LOG_COLUMN_SECOND, record.getTimeReadSec());
            db.insert(TABLE_LOG, null, values);
        }
        db.close();
    }

    public static void DeleteDatabase(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }

}