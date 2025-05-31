package com.example.fetchrewardsexercisejava;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.fetchrewardsexercisejava.ui.RecyclerViewAdapter;
import com.example.fetchrewardsexercisejava.ui.RecyclerViewAdapter;
import com.example.fetchrewardsexercisejava.model.HiringObject;
import com.example.fetchrewardsexercisejava.model.HiringObjectViewModel;
import com.example.fetchrewardsexercisejava.network.HiringObjectApiService;
import com.example.fetchrewardsexercisejava.network.HiringObjectApiCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private HiringObjectViewModel hiringObjectViewModel;
    private static List<HiringObject> hiringObjectList;

    private FloatingActionButton floatingActionButtonRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        hiringObjectViewModel = new ViewModelProvider(this)
                .get(HiringObjectViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);

        // setup adapter
        recyclerViewAdapter = new RecyclerViewAdapter( hiringObjectList);
//        hiringObjectViewModel = new HiringObjectViewModel(this, hiringObjectList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        floatingActionButtonRefresh = findViewById(R.id.refreshFab);
        hiringObjectViewModel.getAllHiringObjects().observe(this, new Observer<List<HiringObject>>() {
            @Override
            public void onChanged(List<HiringObject> hiringObjects) {
                // update the cached copy of hiringObjrcts in the adapter
                recyclerViewAdapter.setHiringObjects(hiringObjects);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}