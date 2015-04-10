// AddEditMovie.java
// Activity for adding a new entry to or
// editing an existing entry in the movie collection.

package kelley.scanlon.moviecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddEditMovie extends Activity{
    private long rowID; // id of movie being edited, if any

    // EditTexts for movie information
    private EditText titleEditText;
    private EditText yearEditText;
    private EditText directorEditText;
    private EditText ratingEditText;
    private EditText viewsEditText;
    private EditText notesEditText;

    // called when the Activity is first started
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // call super's onCreate
        setContentView(R.layout.add_movie); // inflate the UI

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        yearEditText = (EditText) findViewById(R.id.yearEditText);
        directorEditText = (EditText) findViewById(R.id.directorEditText);
        ratingEditText = (EditText) findViewById(R.id.ratingEditText);
        viewsEditText = (EditText) findViewById(R.id.viewsEditText);
        notesEditText = (EditText) findViewById(R.id.notesEditText);

        Bundle extras = getIntent().getExtras(); // get Bundle of extras

        // if there are extras, use them to populate the EditTexts
        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            titleEditText.setText(extras.getString("title"));
            yearEditText.setText(extras.getString("year"));
            directorEditText.setText(extras.getString("director"));
            ratingEditText.setText(extras.getString("rating"));
            viewsEditText.setText(extras.getString("views"));
            notesEditText.setText(extras.getString("notes"));
        } // end if

        // set event listener for the Save Movie Button
        Button saveMoviesButton =
                (Button) findViewById(R.id.saveMovieButton);
        saveMoviesButton.setOnClickListener(saveMovieButtonButtonClicked);
    } // end method onCreate

    // responds to event generated when user clicks the Done Button
    OnClickListener saveMovieButtonButtonClicked = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (titleEditText.getText().length() != 0)
            {
                AsyncTask<Object, Object, Object> saveMovieTask =
                        new AsyncTask<Object, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Object... params)
                            {
                                saveMovie(); // save movie to the database
                                return null;
                            } // end method doInBackground

                            @Override
                            protected void onPostExecute(Object result)
                            {
                                finish(); // return to the previous Activity
                            } // end method onPostExecute
                        }; // end AsyncTask

                // save the movie to the database using a separate thread
                saveMovieTask.execute((Object[]) null);
            } // end if
            else
            {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(AddEditMovie.this);

                // set dialog title & message, and provide Button to dismiss
                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show(); // display the Dialog
            } // end else
        } // end method onClick
    }; // end OnClickListener saveMovieButtonClicked

    // saves movie information to the database
    private void saveMovie()
    {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        if (getIntent().getExtras() == null)
        {
            // insert the movie information into the database - new movie
            databaseConnector.insertMovie(
                titleEditText.getText().toString(),
                yearEditText.getText().toString(),
                directorEditText.getText().toString(),
                ratingEditText.getText().toString(),
                viewsEditText.getText().toString(),
                notesEditText.getText().toString());
        } // end if
        else //edit the movie
        {
            databaseConnector.updateMovie(rowID,
                    titleEditText.getText().toString(),
                    yearEditText.getText().toString(),
                    directorEditText.getText().toString(),
                    ratingEditText.getText().toString(),
                    viewsEditText.getText().toString(),
                    notesEditText.getText().toString());
        } // end else
    } // end class saveMovie

}
