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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ufc_houses.adapter.ArticleAdapter;
import com.example.ufc_houses.adapter.FightAdapter;
import com.example.ufc_houses.model.ModelArticle;
import com.example.ufc_houses.model.ModelFight;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imgHeadlines;
    private RecyclerView fightRecyclerView, articleRecyclerView;
    private FightAdapter fightAdapter;
    private ArticleAdapter articleAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView hamburgerButton;

    LinearProgressIndicator progressIndicator;
    private List<ModelFight> fightList;
    private List<ModelArticle> articleList;
    private DatabaseReference bannerRef, fightsRef, articlesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        hamburgerButton = findViewById(R.id.hamburger_toggle);

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
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_search) {
                    // Handle other navigation items here
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    // ...
                }

                return false;
            }
        });


        imgHeadlines = findViewById(R.id.img_headlines);
        fightRecyclerView = findViewById(R.id.fight_recycler_view);
        articleRecyclerView = findViewById(R.id.article_recycler_view);
        progressIndicator = findViewById(R.id.progress_bar);
        TextView seeTextView = findViewById(R.id.see);

        fightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set horizontal LinearLayoutManager for fightRecyclerView
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        fightRecyclerView.setLayoutManager(horizontalLayoutManager);

        fightList = new ArrayList<>();
        articleList = new ArrayList<>();

        fightAdapter = new FightAdapter(fightList, this);
        articleAdapter = new ArticleAdapter(articleList, this);

        fightRecyclerView.setAdapter(fightAdapter);
        articleRecyclerView.setAdapter(articleAdapter);

        bannerRef = FirebaseDatabase.getInstance().getReference("BannerImageLink");
        fightsRef = FirebaseDatabase.getInstance().getReference("UpcomingFight");
        articlesRef = FirebaseDatabase.getInstance().getReference("Article");

        // Original text, mengatur agar HOT menjadi merah START
        String text = "SEE WHAT'S HOT";

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(text);

        // Find the start and end index of the word "HOT"
        int start = text.indexOf("HOT");
        int end = start + "HOT".length();

        // Set the color span
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the SpannableString to the TextView
        seeTextView.setText(spannableString);
        //mengatur agar HOT menjadi merah END

        getCurrentBannerLink();
        getUpcomingFights();
        getLatestArticles();
    }

    private void getCurrentBannerLink() {
        showProgressIndicator(true);
        bannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showProgressIndicator(false);
                if (dataSnapshot.exists()) {
                    // Assuming there's only one banner link
                    String bannerKey = dataSnapshot.getChildren().iterator().next().getKey();
                    String currentLink = dataSnapshot.child(bannerKey).child("Link").getValue(String.class);

                    if (currentLink != null) {
                        // Set the image src to the current banner link using Picasso
                        Picasso.get()
                                .load(currentLink)
                                .placeholder(R.drawable.launcherlogoufc)
                                .error(R.drawable.no_image)
                                .into(imgHeadlines, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Image loaded successfully
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Handle error while loading image
                                        Log.e("PicassoError", "Error loading image", e);
                                    }
                                });
                    }
                } else {
                    Log.d("Firebase", "No data available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showProgressIndicator(false);
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void getUpcomingFights() {
        showProgressIndicator(true);
        fightsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showProgressIndicator(false);
                fightList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelFight fight = snapshot.getValue(ModelFight.class);
                    if (fight != null) {
                        fightList.add(fight);
                    }
                }
                Collections.reverse(fightList); // Reverse the list to show the most recent fight first
                fightAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showProgressIndicator(false);
                Log.d("Firebase", "Database error: " + databaseError.getMessage());
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
