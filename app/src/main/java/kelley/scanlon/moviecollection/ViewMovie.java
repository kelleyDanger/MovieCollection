// ViewMovie.java
// Activity for viewing a single movie.

package kelley.scanlon.moviecollection;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewMovie  extends ActionBarActivity {

    private long rowID; // selected movie's title
    private TextView titleTextView; // displays movie's title
    private TextView yearTextView; // displays movie's year
    private TextView directorTextView; // displays movie's director
    private TextView ratingTextView; // displays movie's rating
    private TextView viewsTextView; // displays movie's views
    private TextView notesTextView; // displays movie's notes


    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_movie);

        // get the EditTexts
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        yearTextView = (TextView) findViewById(R.id.yearTextView);
        directorTextView = (TextView) findViewById(R.id.directorTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        viewsTextView = (TextView) findViewById(R.id.viewsTextView);
        notesTextView = (TextView) findViewById(R.id.notesTextView);

        // get the selected movie's row ID
        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(MovieCollection.ROW_ID);
    } // end method onCreate

    // called when the activity is first created
    @Override
    protected void onResume()
    {
        super.onResume();

        // create new LoadMovieTask and execute it
        new LoadMovieTask().execute(rowID);
    } // end method onResume

    // performs database query outside GUI thread
    private class LoadMovieTask extends AsyncTask<Long, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(ViewMovie.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOneMovie(params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int titleIndex = result.getColumnIndex("title");
            int yearIndex = result.getColumnIndex("year");
            int directorIndex = result.getColumnIndex("director");
            int ratingIndex = result.getColumnIndex("rating");
            int viewsIndex = result.getColumnIndex("views");
            int notesIndex = result.getColumnIndex("notes");

            // fill TextViews with the retrieved data
            titleTextView.setText(result.getString(titleIndex));
            yearTextView.setText(result.getString(yearIndex));
            directorTextView.setText(result.getString(directorIndex));
            ratingTextView.setText(result.getString(ratingIndex));
            viewsTextView.setText(result.getString(viewsIndex));
            notesTextView.setText(result.getString(notesIndex));

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadMovieTask

    // create the Activity's menu from a menu resource XML file
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_movie_menu, menu);
        return true;
    } // end method onCreateOptionsMenu

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) // switch based on selected MenuItem's ID
        {
            case R.id.editItem:
                // create an Intent to launch the AddEditMovie Activity
                Intent addEditMovie =
                        new Intent(this, AddEditMovie.class);

                // pass the selected contact's data as extras with the Intent
                addEditMovie.putExtra(MovieCollection.ROW_ID, rowID);
                addEditMovie.putExtra("title", titleTextView.getText());
                addEditMovie.putExtra("year", yearTextView.getText());
                addEditMovie.putExtra("director", directorTextView.getText());
                addEditMovie.putExtra("rating", ratingTextView.getText());
                addEditMovie.putExtra("views", viewsTextView.getText());
                addEditMovie.putExtra("notes", notesTextView.getText());
                startActivity(addEditMovie); // start the Activity
                return true;
            case R.id.deleteItem:
                deleteMovie(); // delete the displayed contact
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end method onOptionsItemSelected

    // delete a movie
    private void deleteMovie()
    {
        // create a new AlertDialog Builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ViewMovie.this);

        builder.setTitle(R.string.confirmTitle); // title bar string
        builder.setMessage(R.string.confirmMessage); // message to display

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.button_delete,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(ViewMovie.this);

                        // create an AsyncTask that deletes the movie in another
                        // thread, then calls finish after the deletion
                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(Long... params)
                                    {
                                        databaseConnector.deleteMovie(params[0]);
                                        return null;
                                    } // end method doInBackground

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        finish(); // return to the MovieCollection Activity
                                    } // end method onPostExecute
                                }; // end new AsyncTask

                        // execute the AsyncTask to delete movie at rowID
                        deleteTask.execute(rowID);
                    } // end method onClick
                } // end anonymous inner class
        ); // end call to method setPositiveButton

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show(); // display the Dialog
    } // end method deleteMovie
} //end class ViewMovie
