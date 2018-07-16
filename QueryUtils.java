import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    /* Return a list of Article objects that has been built up from parsing a JSON response. */
    private static List<Article> extractResultsFromJson(String articleJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        /* Create an empty ArrayList that we can start adding articles to */

        List<Article> articles = new ArrayList<>();

        /* Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON

        is formatted, a JSONException exception object will be thrown.

        Catch the exception so the app doesn't crash, and print the error message to the logs. */

        try {

            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            JSONObject ArticleJson = baseJsonResponse.getJSONObject("response");

            JSONArray articleArray = ArticleJson.getJSONArray("results");

            /* Create a JSONObject from the GUARDIAN_JSON_RESPONSE string */

            //find the "results" array

            for (int i = 0; i < articleArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);

                // Extract the JSONArray associated with the key called "results",

                // which represents a list of results (or articles)

                /* For each article in the resultsArray, create an Article object */

                // Extract the value for the key called "sectionName"
                String sectionName = currentArticle.getString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentArticle.getString("webPublicationDate");

                // Extract the value for the key called "webTitle"
                String webTitle = currentArticle.getString("webTitle");

                //Extract the value for the key called "webURL"
                String webUrl = currentArticle.getString("webUrl");

                JSONArray tags = articleArray.getJSONObject(i).getJSONArray("tags");

                String author="";

                if (tags.length()>0) {

                    author = tags.getJSONObject(0).getString("webTitle");

                }
                /* Create a new Article object with the webTitle, sectionName, webPublicationDate, webUrl ,

                 and author from the JSON response. */

                Article article = new Article( sectionName, webPublicationDate, webTitle, author, webUrl );

                // Add the new {@link Article} to the list of articles.

                articles.add(article);
            }

        } catch (JSONException e) {

            // If an error is thrown when executing any of the above statements in the "try" block,

            // catch the exception here, so the app doesn't crash. Print a log message

            // with the message from the exception.

            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

    // Query the GUARDIAN dataset and return an {@link Article} object to represent a single article.

    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Articles}
        List<Article> results = extractResultsFromJson(jsonResponse);

        // Return the list of {@link Results}
        return results;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate (Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        return dateFormat.format(dateObject);
    }
}

