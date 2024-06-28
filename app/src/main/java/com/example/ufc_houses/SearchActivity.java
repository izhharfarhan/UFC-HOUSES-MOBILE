package com.example.ufc_houses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        drawerLayout = findViewById(R.id.drawer_layout_search);
        navigationView = findViewById(R.id.nav_view);
        hamburgerButton = findViewById(R.id.hamburger_toggle);

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
                    // Start SearchActivity when nav_home is clicked
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_search) {
                    // Handle other navigation items here
                    Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                    startActivity(intent);
                    // ...
                }

                return false;
            }
        });

        getLatestArticles();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showProgressIndicator(false);
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    //PROGRESSBAR ATAU LOADING
    private void showProgressIndicator(boolean show) {
        if (show) {
            progressIndicator.setVisibility(View.VISIBLE);
        } else {
            progressIndicator.setVisibility(View.GONE);
        }
    }

}