package com.example.share_eco;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share_eco.databinding.ActivityMainBinding;
import com.example.share_eco.models.mModels;
import com.example.share_eco.viewHolders.mViewHolders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView rvCategory, rvFeatured;
    private RecyclerViewClickListener listener;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mFeaturedRef, mCategoryRef;
    private TextView searchView;
    private ProgressBar progressBarFeatured;
    private EditText editTextSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        mFeaturedRef = FirebaseDatabase.getInstance().getReference("beauty_connect").child("featured");
        mFeaturedRef = FirebaseDatabase.getInstance().getReference("share-eco").child("products").child("featured");
        searchView = findViewById(R.id.sv_search);
        editTextSearch = findViewById(R.id.et_search);

        //handle the categories recyclerview
        rvCategory = findViewById(R.id.rv_categories);
        rvCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCategory.setLayoutManager(layoutManager);

        //handle the featured recyclerview
        rvFeatured = findViewById(R.id.rv_featured);
        rvFeatured.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFeatured.setLayoutManager(layoutManager);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //load
        loadFeaturedProducts();
        loadCategories();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editTextSearch.getText().toString().toLowerCase(Locale.ROOT).trim();

                filter (input);
            }
        });
    }

    private void filter(String input) {
        Query query = FirebaseDatabase.getInstance().getReference("share-eco").child("products").child("featured");
        FirebaseRecyclerOptions<mModels> options = new FirebaseRecyclerOptions.Builder<mModels>()
                .setQuery(query.orderByChild("name").equalTo(input), mModels.class)
                .build();
        FirebaseRecyclerAdapter<mModels, mViewHolders> mAdapter3 =
                new FirebaseRecyclerAdapter<mModels, mViewHolders>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final mViewHolders holder, int i, @NonNull final mModels model) {

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Picasso.get().load(model.getImage()).into(holder.ivFeaturedPhoto);
                                holder.txtFeaturedCategory.setText(model.getCategory());
                                holder.txtName.setText(model.getName());
                                holder.txtRate.setText("K" + model.getRate() + " per day.");
                                holder.txtDescription.setText(model.getDescription());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public mViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_featured_list_item, parent, false);
                        mViewHolders holder = new mViewHolders(view);
                        return holder;
                    }
                };
        setOnClickListener();
        rvFeatured.setAdapter(mAdapter3);
        mAdapter3.startListening();
    }

    private void loadCategories() {
        Query query = FirebaseDatabase.getInstance().getReference("share-eco").child("categories");
        FirebaseRecyclerOptions<mModels> options = new FirebaseRecyclerOptions.Builder<mModels>()
                .setQuery(query, mModels.class)
                .build();
        FirebaseRecyclerAdapter<mModels, mViewHolders> mAdapter3 =
                new FirebaseRecyclerAdapter<mModels, mViewHolders>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final mViewHolders holder, int i, @NonNull final mModels model) {
                        holder.txtCategory.setText(model.getName());

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    //show label
                                    int count = (int) snapshot.getChildrenCount();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public mViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list_item, parent, false);
                        mViewHolders holder = new mViewHolders(view);
                        return holder;
                    }
                };
        setOnClickListener();
        rvCategory.setAdapter(mAdapter3);
        mAdapter3.startListening();
    }

    private void loadFeaturedProducts() {

        Query query = FirebaseDatabase.getInstance().getReference("share-eco").child("products").child("featured");
        FirebaseRecyclerOptions<mModels> options = new FirebaseRecyclerOptions.Builder<mModels>()
                .setQuery(query.orderByChild("postCount"), mModels.class)
                .build();
        FirebaseRecyclerAdapter<mModels, mViewHolders> mAdapter3 =
                new FirebaseRecyclerAdapter<mModels, mViewHolders>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final mViewHolders holder, int i, @NonNull final mModels model) {
                        Picasso.get().load(model.getImage()).into(holder.ivFeaturedPhoto);
                        holder.txtFeaturedCategory.setText(model.getCategory());
                        holder.txtName.setText(model.getName());
                        holder.txtRate.setText("K" + model.getRate() + " per day.");
                        holder.txtDescription.setText(model.getDescription());

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public mViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_featured_list_item, parent, false);
                        mViewHolders holder = new mViewHolders(view);
                        return holder;
                    }
                };
        setOnClickListener();
        rvFeatured.setAdapter(mAdapter3);
        mAdapter3.startListening();
    }


    private void setOnClickListener() {
        listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

            }
        };
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}