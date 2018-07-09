package com.example.android.newsappstage2;

public class Article {

    //the name of the section/
    private String mSectionName;
    //the date of the article/
    private String mWebPublicationDate;
    //the title of the article/
    private String mWebTitle;
    //the author of the article/
    private String mAuthor;
    //the url of the article/
    private String mWebUrl;

    /**construct a new (@link Article} object .
     *@param author is the author of the article.
     *@param sectionName is the name of the sectionName of the article.
     *@param webPublicationDate is the date of the article.
     *@param webTitle is the title of the article.
     *@param webUrl is the url of the article.   */

    public Article(String sectionName, String webPublicationDate, String webTitle, String author, String webUrl) {
        mSectionName = sectionName;
        mWebPublicationDate = webPublicationDate;
        mWebTitle = webTitle;
        mAuthor = author;
        mWebUrl = webUrl;
    }

    //returns the section of the article/
    public String getSection() {return mSectionName;}

    //returns the date of the article/
    public String getDate() {return mWebPublicationDate;}

    //returns the title of the article/
    public String getTitle() {return mWebTitle;}

    //returns the author of the article/
    public String getAuthor() {return mAuthor;}

    //returns the url of the article/
    public String getUrl() {return mWebUrl;}

}



