// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package kelley.scanlon.moviecollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector
{
    // database name
    private static final String DATABASE_NAME = "MovieCollection";
    private SQLiteDatabase database; // database object
    private DatabaseOpenHelper databaseOpenHelper; // database helper

    // public constructor for DatabaseConnector
    public DatabaseConnector(Context context)
    {
        // create a new DatabaseOpenHelper
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    } // end DatabaseConnector constructor

    // open the database connection
    public void open() throws SQLException
    {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    } // end method open

    // close the database connection
    public void close()
    {
        if (database != null)
            database.close(); // close the database connection
    } // end method close

    // inserts a new movie in the database
    public void insertMovie(String title, String year, String director,
                            String rating, String views, String notes)
    {
        ContentValues newMovie = new ContentValues();
        newMovie.put("title", title);
        newMovie.put("year", year);
        newMovie.put("director", director);
        newMovie.put("rating", rating);
        newMovie.put("views", views);
        newMovie.put("notes", notes);

        open(); // open the database
        database.insert("Movies", null, newMovie);
        close(); // close the database
    } // end method insertMovie

    // edits a movie in the database
    public void updateMovie(long id, String title, String year, String director,
                            String rating, String views, String notes)
    {
        ContentValues updateMovie = new ContentValues();
        updateMovie.put("title", title);
        updateMovie.put("year", year);
        updateMovie.put("director", director);
        updateMovie.put("rating", rating);
        updateMovie.put("views", views);
        updateMovie.put("notes", notes);

        open(); // open the database
        database.update("Movies", updateMovie, "_id=" + id, null);
        close(); // close the database
    } // end method updateMovie

    // return a Cursor with all movie titles in the database
    public Cursor getAllMovies(String sort)
    {
        // tableName, returnColumns, selection, selectionArgs, groupBy, having, orderBy
        return database.query("Movies", new String[] {"_id", "title"},
                null, null, null, null, "title " + sort);
    } // end method getAllMovies

    // get a Cursor containing all information about the movie specified by the given id
    public Cursor getOneMovie(long id)
    {
        // tableName, returnColumns (null = all cols), selection, selectionArgs, groupBy, having, orderBy
        return database.query(
                "Movies", null, "_id=" + id, null, null, null, null);
    } // end method getOneMovie

    // delete the movie specified by the given String title
    public void deleteMovie(long id)
    {
        open(); // open the database
        // table, whereClause, whereArgs
        database.delete("Movies", "_id=" + id, null);
        close(); // close the database
    } // end method deleteMovie

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        // public constructor
        public DatabaseOpenHelper(Context context, String name,
                                  CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        } // end DatabaseOpenHelper constructor

        // creates the Movies table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // query to create a new table named Movies
            String createQuery = "CREATE TABLE Movies" +
                    "(_id integer primary key autoincrement," +
                    "title TEXT, " +
                    "year TEXT, " +
                    "director TEXT," +
                    "rating TEXT, " +
                    "views TEXT, " +
                    "notes TEXT);";

            db.execSQL(createQuery); // execute the query
        } // end method onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        } // end method onUpgrade
    } // end class DatabaseOpenHelper
} // end class DatabaseConnector

