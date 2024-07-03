package com.example.ufc_houses;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ufc_houses.adapter.ArticleAdapter;
import com.example.ufc_houses.model.ModelArticle;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView hamburgerButton;

    private RecyclerView articleRecyclerView;

    private ArticleAdapter articleAdapter;

    private List<ModelArticle> articleList;

    private DatabaseReference articlesRef;

    LinearProgressIndicator progressIndicator;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        drawerLayout = findViewById(R.id.drawer_layout_search);
        navigationView = findViewById(R.id.nav_view);
        hamburgerButton = findViewById(R.id.hamburger_toggle);
        searchEditText = findViewById(R.id.searchEditText);

        articleRecyclerView = findViewById(R.id.article_recycler_view);
        progressIndicator = findViewById(R.id.progress_bar);

        articleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        articleList = new ArrayList<>();

        articleAdapter = new ArticleAdapter(articleList, this);

        articleRecyclerView.setAdapter(articleAdapter);

        articlesRef = FirebaseDatabase.getInstance().getReference("Article");

        // Set onClickListener for hamburger button
        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END, true);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_search) {
                    Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        getLatestArticles();

        // Add TextWatcher to searchEditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterArticles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Category buttons click listeners
        setupCategoryButtons();
    }

    private void setupCategoryButtons() {
        Button btnAll = findViewById(R.id.btn_1);
        Button btnLightHeavyweight = findViewById(R.id.btn_2);
        Button btnLightweight = findViewById(R.id.btn_3);
        Button btnMiddleweight = findViewById(R.id.btn_4);
        Button btnHeavyweight = findViewById(R.id.btn_5);
        Button btnOther = findViewById(R.id.btn_6);

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestArticles(); // Show all articles
            }
        });

        btnLightHeavyweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterArticles("Light-Heavyweight"); // Filter by Light-Heavyweight category
            }
        });

        btnLightweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterArticles("Lightweight"); // Filter by Lightweight category
            }
        });

        btnMiddleweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterArticles("Middleweight"); // Filter by Middleweight category
            }
        });

        btnHeavyweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterArticles("Heavyweight"); // Filter by Heavyweight category
            }
        });

        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterArticles("Other"); // Filter by Other category
            }
        });
    }

    private void getLatestArticles() {
        showProgressIndicator(true);
        articlesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showProgressIndicator(false);
                articleList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelArticle article = snapshot.getValue(ModelArticle.class);
                    if (article != null) {
                        articleList.add(article);
                    }
                }
                Collections.reverse(articleList); // Reverse the list to show the most recent article first
                articleAdapter.notifyDataSetChanged();

                // Menambahkan GlobalLayoutListener untuk mengukur tinggi RecyclerView
                articleRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int totalHeight = 0;
                        for (int i = 0; i < articleAdapter.getItemCount(); i++) {
                            View listItem = articleAdapter.onCreateViewHolder(articleRecyclerView, articleAdapter.getItemViewType(i)).itemView;
                            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            totalHeight += listItem.getMeasuredHeight();
                            Log.d("ItemHeight", "Item " + i + " height: " + listItem.getMeasuredHeight());
                        }
                        ViewGroup.LayoutParams params = articleRecyclerView.getLayoutParams();
                        int additionalHeight = (int) (150 * getResources().getDisplayMetrics().density); // Convert 50dp to pixels
                        params.height = totalHeight + (articleRecyclerView.getItemDecorationCount() * 10) + additionalHeight; // Adjust for item decorations if any
                        articleRecyclerView.setLayoutParams(params);

                        Log.d("TotalHeight", "Total RecyclerView height: " + params.height);
                        articleRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showProgressIndicator(false);
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }


    private void showProgressIndicator(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void filterArticles(String query) {
        List<ModelArticle> filteredList = new ArrayList<>();

        for (ModelArticle article : articleList) {
            if (article.getArticleTitle().toLowerCase().contains(query.toLowerCase()) ||
                    article.getArticleCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(article);
            }
        }

        articleAdapter.updateArticleList(filteredList);
    }
}
