package kelley.scanlon.moviecollection;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class MovieCollection extends ActionBarActivity {

    // key-value pair passed values between activities
    public static final String ROW_ID = "row_id";

    private ListView moviesListView;
    private Button sortButton;

    // adapter for populating the ListView
    private CursorAdapter movieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_collection);

        moviesListView = (ListView)findViewById(R.id.listView);
        moviesListView.setOnItemClickListener(viewMovieListener);
        moviesListView.setBackgroundColor(Color.BLACK);

        // display message on an empty list
        TextView emptyText = (TextView)View.inflate(this, R.layout.movie_list_empty, null);

        emptyText.setVisibility(View.GONE); // will be automatically toggled by listview

        ((ViewGroup)moviesListView.getParent()).addView(emptyText);
        moviesListView.setEmptyView(emptyText);
        // end of display empty list code

        // map each movie title to a TextView in the ListView layout
        String[] from = new String[]{"title"};
        int[] to = new int[]{R.id.movieTextView};
        // contextOfListView,
        // layoutFile (w/ views defined in 'to'),
        // dbCursor,
        // colNameList,
        // displayView(w/ col in 'from'),
        // flags
        movieAdapter = new SimpleCursorAdapter(
                MovieCollection.this, R.layout.movie_list_item, null, from, to, 0
        ); // 0 = start at first row

        moviesListView.setAdapter(movieAdapter);

        // get reference to Sort button
        sortButton = (Button) findViewById(R.id.sortButton);
        sortButton.setOnClickListener(sortListener);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // create a new GetMoviesTask and execute
        new GetMoviesTaskAsc().execute((Object[])null);

    }

    @Override
    protected void onStop(){
        Cursor cursor = movieAdapter.getCursor(); // get cursor

        if(cursor != null){
            //we have a cursor
            //so lets close - deactivate it
            cursor.close();
        }
        movieAdapter.changeCursor(null); // adapter has no cursor
        super.onStop(); //needs to be last because other code must execute first
    }

    //perform the database query outside the GUI Thread
    private class GetMoviesTaskAsc extends AsyncTask<Object, Object, Cursor>{
        DatabaseConnector databaseConnector = new DatabaseConnector(MovieCollection.this);

        //perform the database access
        @Override // from AsyncTask Class
        protected Cursor doInBackground(Object... params){
            databaseConnector.open();

            //get cursor containing all movies
            return databaseConnector.getAllMovies("ASC");
        }

        // use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result){
            movieAdapter.changeCursor(result); //set the adapters cursor
            databaseConnector.close(); // close connection
        }

    }

    //perform the database query outside the GUI Thread
    private class GetMoviesTaskDesc extends AsyncTask<Object, Object, Cursor>{
        DatabaseConnector databaseConnector = new DatabaseConnector(MovieCollection.this);

        //perform the database access
        @Override // from AsyncTask Class
        protected Cursor doInBackground(Object... params){
            databaseConnector.open();

            //get cursor containing all movies
            return databaseConnector.getAllMovies("DESC");
        }

        // use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result){
            movieAdapter.changeCursor(result); //set the adapters cursor
            databaseConnector.close(); // close connection
        }

    }

    // when an object is instantiated, does the compiler keep all methods and properties
    // for a given object each time it is instantiated or just the ones that are used


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_collection, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //create new intent to launch the AddEditMovie Activity

        Intent addNewMovie = new Intent(MovieCollection.this, AddEditMovie.class); // context to current activity, new activity class
        startActivity(addNewMovie); // this starts the activity

        return super.onOptionsItemSelected(item);
    }

    OnItemClickListener viewMovieListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // create and Intent to launch the ViewMovie Activity
            Intent viewMovie = new Intent(MovieCollection.this, ViewMovie.class);

            //pass the selected contacts row ID as extra with the Intent
            viewMovie.putExtra(ROW_ID, id);
            startActivity(viewMovie);
        }
    }; //end OnItemClick

    // sortListener
    public OnClickListener sortListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // grab text and decide which to sort by
            String sortBy = sortButton.getText().toString();

            // run sort query
            if(sortBy.equals("Sort Desc")) {
                Toast.makeText(getApplicationContext(), "Clicked Sorted By Desc", Toast.LENGTH_SHORT).show();

                // sort by desc
                new GetMoviesTaskDesc().execute((Object[])null);

                // change button text
                sortButton.setText("Sort Asc");

            } else if(sortBy.equals("Sort Asc")) {
                Toast.makeText(getApplicationContext(), "Clicked Sorted By Asc", Toast.LENGTH_SHORT).show();

                // sort by Asc
                new GetMoviesTaskAsc().execute((Object[])null);


                // change button text
                sortButton.setText("Sort Desc");
            }

            // change button text to 'Sort Asc' / 'Sort Desc'

        }
    };
}
