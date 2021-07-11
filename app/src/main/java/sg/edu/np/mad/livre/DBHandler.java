package sg.edu.np.mad.livre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Livre.db";
    public static final String TABLE_BOOK = "Book";
    public static final String BOOK_COLUMN_ID = "_id";
    public static final String BOOK_COLUMN_ISBN = "ISBN";
    public static final String BOOK_COLUMN_AUTHOR = "AUTHOR";
    public static final String BOOK_COLUMN_BLURB = "BLURB";
    public static final String BOOK_COLUMN_THUMBNAIL = "THUMBNAIL";
    public static final String BOOK_COLUMN_READING_TIME = "READING_TIME";
    public static final String BOOK_COLUMN_CUSTOM = "CUSTOM";
    public static final String BOOK_COLUMN_ARCHIVED = "ARCHIVED";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE " + TABLE_BOOK + "(" + BOOK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BOOK_COLUMN_ISBN + " TEXT," + BOOK_COLUMN_AUTHOR + "TEXT" + BOOK_COLUMN_BLURB + " TEXT," + BOOK_COLUMN_THUMBNAIL + " TEXT," + BOOK_COLUMN_READING_TIME + " INT," + BOOK_COLUMN_CUSTOM + " INT," + BOOK_COLUMN_ARCHIVED + " INT" + ")";
        db.execSQL(CREATE_BOOK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
        onCreate(db);
    }

    public void AddBook(Book book){
        ContentValues values = new ContentValues();
        values.put(BOOK_COLUMN_ISBN, book.getIsbn());
        values.put(BOOK_COLUMN_AUTHOR, book.getAuthor());
        values.put(BOOK_COLUMN_BLURB, book.getBlurb());
        values.put(BOOK_COLUMN_THUMBNAIL, book.getThumbnail());
        values.put(BOOK_COLUMN_READING_TIME, book.getReadSeconds());
        values.put(BOOK_COLUMN_CUSTOM, book.isCustom()? 1: 0);
        values.put(BOOK_COLUMN_ARCHIVED, book.isArchived()? 1: 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_BOOK, null, values);
        db.close();
    }

    public Book FindBookByISBN(String ISBN){
        String dbQuery = "SELECT * FROM " + TABLE_BOOK + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + ISBN +"\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(dbQuery, null);
        Book book = new Book();
        if (cursor.moveToFirst()){
            book.setIsbn(cursor.getString(1));
            book.setAuthor(cursor.getString(2));
            book.setBlurb(cursor.getString(3));
            book.setThumbnail(cursor.getString(4));
            book.setReadSeconds(cursor.getInt(5));
            book.setCustom(cursor.getInt(6) == 1? true: false);
            book.setArchived(cursor.getInt(7) == 1? true: false);
        } else {
            book = null;
        }
        return book;
    }

    public void ToggleArchive(Book book){
        SQLiteDatabase db = this.getWritableDatabase();
//        String dbQuery = "UPDATE " + TABLE_BOOK + " SET " + BOOK_COLUMN_ARCHIVED + " = " + (book.isArchived? 1: 0) + " WHERE " + BOOK_COLUMN_ISBN + " = \"" + book.getIsbn() + "\"";
//        db.execSQL(dbQuery);
        ContentValues values = new ContentValues();
        values.put(BOOK_COLUMN_ARCHIVED, book.isArchived()? 1: 0);
        int rowsAffected = db.update(TABLE_BOOK, values, BOOK_COLUMN_ISBN + " = " + book.getIsbn(), null);
        db.close();
    }

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
                book.setName(cursor.getString(2));
                book.setBlurb(cursor.getString(3));
                book.setThumbnail(cursor.getString(4));
                book.setReadSeconds(cursor.getInt(5));
                book.setCustom(cursor.getInt(6) == 1? true: false);
                book.setArchived(cursor.getInt(7) == 1? true: false);
                bookList.add(book);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            bookList = null;
        }
        db.close();
        return bookList;
    }

}
