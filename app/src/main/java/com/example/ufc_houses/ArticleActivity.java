package com.example.ufc_houses;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ArticleActivity extends AppCompatActivity {

    private TextView titleTextView, categoryTextView, dateTextView, contentTextView;
    private ImageView thumbnailImageView, hamburgerButton;
    private LinearProgressIndicator progressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        titleTextView = findViewById(R.id.title_article);
        categoryTextView = findViewById(R.id.article_category);
        dateTextView = findViewById(R.id.article_date);
        contentTextView = findViewById(R.id.content_article);
        thumbnailImageView = findViewById(R.id.article_image_view);
        progressBar = findViewById(R.id.progress_bar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        hamburgerButton = findViewById(R.id.hamburger_toggle);

        // Display progress bar while loading data
        progressBar.show();

        // Get data from intent
        String title = getIntent().getStringExtra("articleTitle");
        String category = getIntent().getStringExtra("articleCategory");
        String date = getIntent().getStringExtra("articleDate");
        String thumbnail = getIntent().getStringExtra("articleThumbnail");
        String content = getIntent().getStringExtra("articleContent");

        // Set data to views
        titleTextView.setText(title);
        categoryTextView.setText(category);
        dateTextView.setText(date);
        contentTextView.setText(content);

        // Load image with Picasso and hide progress bar once image is loaded
        Picasso.get().load(thumbnail).into(thumbnailImageView, new Callback() {
            @Override
            public void onSuccess() {
                // Hide progress bar when image is successfully loaded
                progressBar.hide();
            }

            @Override
            public void onError(Exception e) {
                // Hide progress bar even if there's an error
                progressBar.hide();
            }
        });

        // Hide progress bar when all text data is set
        titleTextView.post(() -> progressBar.hide());

        // Set onClickListener for hamburger button
        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the drawer when hamburger button is clicked
                drawerLayout.openDrawer(GravityCompat.END, true);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                if (item.getItemId() == R.id.nav_home) {
                    // Start MainActivity when nav_home is clicked
                    Intent intent = new Intent(ArticleActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_search) {
                    // Start SearchActivity when nav_search is clicked
                    Intent intent = new Intent(ArticleActivity.this, SearchActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }
}
