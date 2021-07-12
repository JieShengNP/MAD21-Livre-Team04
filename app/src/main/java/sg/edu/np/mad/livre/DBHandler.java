package sg.edu.np.mad.livre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    public static final String BOOK_COLUMN_ARCHIVED = "ARCHIVED";
    public static final String TABLE_LOG = "Log";
    public static final String LOG_COLUMN_ISBN = "Isbn";
    public static final String LOG_COLUMN_DATE = "Date";
    public static final String LOG_COLUMN_SECOND = "Time";
    public static final String LOG_COLUMN_ID = "_id";
    public static final String LOG_COLUMN_NAME = "Name";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOK + "(" + BOOK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BOOK_COLUMN_ISBN + " TEXT," + BOOK_COLUMN_AUTHOR + " TEXT," + BOOK_COLUMN_YEAR + " TEXT," + BOOK_COLUMN_TITLE + " TEXT," + BOOK_COLUMN_BLURB + " TEXT," + BOOK_COLUMN_THUMBNAIL + " TEXT," + BOOK_COLUMN_READING_TIME + " INT," + BOOK_COLUMN_CUSTOM + " INT," + BOOK_COLUMN_ARCHIVED + " INT" + ")";
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_LOG + "(" + LOG_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                    + LOG_COLUMN_NAME+ " TEXT," + LOG_COLUMN_ISBN+ " TEXT," + LOG_COLUMN_DATE + " TEXT," + LOG_COLUMN_SECOND + " INT)";
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
        values.put(BOOK_COLUMN_AUTHOR, book.getName());
        values.put(BOOK_COLUMN_YEAR, book.getName());
        values.put(BOOK_COLUMN_BLURB, book.getBlurb());
        values.put(BOOK_COLUMN_THUMBNAIL, book.getThumbnail());
        values.put(BOOK_COLUMN_READING_TIME, book.getReadSeconds());
        values.put(BOOK_COLUMN_CUSTOM, book.isCustom()? 1: 0);
        values.put(BOOK_COLUMN_ARCHIVED, book.isArchived()? 1: 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_BOOK, null, values);
        db.close();
    }

    /**
     * Retriving of Book by its ISBN number.
     * @param ISBN ISBN of the book
     * @return Book if it exists in the Database.
     */
    public Book FindBookByISBN(String ISBN){
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + ISBN +"\"";
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
            book.setArchived(cursor.getInt(9) == 1? true: false);
        } else {
            book = null;
        }
        return book;
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
     * Deletion of book from the Database.
     * @param book The book to be deleted
     * @return
     */
    public boolean DeleteBook(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = false;
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + book.getIsbn() + "\"";
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
                book.setArchived(cursor.getInt(9) == 1? true: false);
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
    public void updateLog(String isbn,int seconds, String name)
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
        db.update(TABLE_BOOK, values, BOOK_COLUMN_ISBN + " = ?",new String[]{book.isbn});
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
                book.setArchived(cursor.getInt(9) == 1? true: false);
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
                book.setArchived(cursor.getInt(9) == 1? true: false);
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
                records.setIsbn(cursor.getString(2));
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
            cursor.close();
        }
        return recordsList;
    }
}
