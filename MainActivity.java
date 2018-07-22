package com.example.android.newsappstage2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsappstage2.R.string.no_articles_found_please_try_again_later;
import static com.example.android.newsappstage2.R.string.no_internet_connection_please_check_your_connectivity_and_try_again;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>, SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */

    private static final int ARTICLE_LOADER_ID = 1;

    /**
     * Adapter for the list of new
     */
    private ArticleAdapter mAdapter;


    // URL to query the Guardian dataset for art and crafts information


    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";


    //TextView that is displayed when the list is empty


    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Find a reference to the {@link ListView} in the layout

        ListView ArticleListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of articles as input
        //
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}

        // so the list can be populated in the user interface

        ArticleListView.setAdapter(mAdapter);

        mEmptyStateTextView = findViewById(R.id.empty_view);

        ArticleListView.setEmptyView(mEmptyStateTextView);


// Set an item click listener on the ListView, which sends an intent to a web browser

// to open a website with more information about the selected earthquake.


        ArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current news that was clicked on

                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)

                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity

                startActivity(websiteIntent);

            }

        });

        // Get a reference to the ConnectivityManager to check state of network connectivity

        ConnectivityManager connMgr = (ConnectivityManager)

                getSystemService(Context.CONNECTIVITY_SERVICE);


        // Get details on the currently active default data network

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data

        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.

            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for

            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid

            // because this activity implements the LoaderCallbacks interface).

            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);

        } else {

            // Otherwise, display error

            // First, hide loading indicator so error message will be visible

            View loadingIndicator = findViewById(R.id.loading_indicator);

            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message

            mEmptyStateTextView.setText(no_internet_connection_please_check_your_connectivity_and_try_again);

        }

    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle args) {

        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //return new ArticleLoader(this, GUARDIAN_REQUEST_URL);


        //getString retrieves a String value from the preferences. The second parameter is the default value for this preference.

        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String section = sharedPrefs.getString(getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("q", "art");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "f5c30d8e-0b2b-4486-afd6-e1583d0520f1");
        uriBuilder.appendQueryParameter("section", "culture");
        uriBuilder.appendQueryParameter("from-date", "2018-01-01");
        uriBuilder.appendQueryParameter("to-date", "2018-06-06");
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("order-by", orderBy);


        //Return the completed uri "https://content.guardianapis.com/search?q=art&show-tags=contributor&api-key=f5c30d8e-0b2b-4486-afd6-e1583d0520f1";


        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> allArticles) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);

        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles found."
        mEmptyStateTextView.setText(no_articles_found_please_try_again_later);

        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Articles}, then add them to the adapter's
        // data set. This will trigger the ListView to update.

        if (allArticles != null && !allArticles.isEmpty()) {

            mAdapter.addAll(allArticles);


        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_order_by_key)) || key.equals(getString(R.string.settings_section_key))) {
            mAdapter.clear();
            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);
            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(ARTICLE_LOADER_ID, null, this);
        }
    }
}
