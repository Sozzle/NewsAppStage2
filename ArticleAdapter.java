package com.example.android.newsappstage2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    private List<Article> articleList;

    public ArticleAdapter(Activity context, List<Article> articles) {
        super(context, 0, articles);
    }

    public void setArticle(List<Article>articleList){
        this.articleList = articleList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// Check if there is an existing list item view (called convertView) that we can reuse,
// otherwise if convertView is null, then inflate a new list item layout
        View listItemView = convertView;
        if (listItemView == null) {

            listItemView = LayoutInflater.from(getContext()).inflate(

                    R.layout.list_item, parent, false);
        }

        //Find the article at the given position in the list of articles.
        Article currentArticle = getItem(position);

        //Find the TextView with ID section
        TextView SectionView = (TextView) listItemView.findViewById(R.id.section);
        //Display the section of the currentArticle in that TextView
        SectionView.setText(currentArticle.getSection());

        //Find the TextView with ID date
        TextView DateView = (TextView) listItemView.findViewById(R.id.date);
        //Display the date of the currentArticle in that TextView
        DateView.setText(currentArticle.getDate());


        //Find the TextView with ID title
        TextView TitleView = (TextView) listItemView.findViewById(R.id.title);
        //Display the title of the current article in that TextView
        TitleView.setText(currentArticle.getTitle());


        //Find the TextView with ID author
        TextView AuthorView = (TextView) listItemView.findViewById(R.id.author);
        //Display the date of the currentAuthor in that TextView
        AuthorView.setText(currentArticle.getAuthor());


        return listItemView;

    }
}



