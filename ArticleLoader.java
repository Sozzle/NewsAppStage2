package com.example.android.newsappstage2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    /**
     * Constructs a new {@link ArticleLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    private String mWebUrl = "";

    public ArticleLoader(Context context, String url) {
        super(context);
        mWebUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * * This is on a background thread.
     */
    @Override
    public List<Article> loadInBackground() {
        if (mWebUrl == null) {
            return null;
        }

        List<Article> articles = (List<Article>) QueryUtils.fetchArticleData(mWebUrl);
        return articles;
    }
}
