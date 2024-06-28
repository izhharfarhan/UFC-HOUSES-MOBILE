package com.example.ufc_houses.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ufc_houses.R;
import com.example.ufc_houses.model.ModelArticle;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<ModelArticle> articleList;
    private Context context;

    public ArticleAdapter(List<ModelArticle> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_recycler_row, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ModelArticle article = articleList.get(position);

        Log.d("ArticleAdapter", "Binding article: " + article.getArticleTitle());

        holder.articleTitle.setText(article.getArticleTitle());
        holder.articleCategory.setText(article.getArticleCategory());
        holder.articleDate.setText(article.getCreatedAt());

        Picasso.get().load(article.getArticleThumbnail()).into(holder.articleImageView);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImageView;
        TextView articleTitle, articleCategory, articleDate;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            articleImageView = itemView.findViewById(R.id.article_image_view);
            articleTitle = itemView.findViewById(R.id.article_title);
            articleCategory = itemView.findViewById(R.id.article_category);
            articleDate = itemView.findViewById(R.id.article_date);
        }
    }
}
