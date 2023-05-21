package com.example.studentdormitoryfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchactivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView searchResultsRecyclerView;

    private DatabaseReference databaseReference;
    private SearchAdapter searchAdapter;

    private FirebaseAuth authProfile;
    private List<SearchItem> searchItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchactivity);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // Initialize Firebase database reference
//        authProfile = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Hostel Owners");

        // Set up RecyclerView
        searchItems = new ArrayList<>();
        searchAdapter = new SearchAdapter(searchItems);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(searchAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                performSearch(query);
            }
        });
    }

    private void performSearch(String query) {
        Query searchQuery = databaseReference.orderByChild("location").startAt(query);
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SearchItem searchItem = snapshot.getValue(SearchItem.class);
                    searchItems.add(searchItem);
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(searchactivity.this, "Search canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
