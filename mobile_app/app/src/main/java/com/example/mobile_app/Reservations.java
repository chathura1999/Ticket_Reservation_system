package com.example.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_app.Model.Train;
import com.example.mobile_app.Remote.IMyAPI;
import com.example.mobile_app.Remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reservations extends AppCompatActivity {

    private RecyclerView recyclerView;
    IMyAPI iMyAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        Intent intent = getIntent();
        String userData = intent.getStringExtra("userData");


        //init API
        iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //call the get Train reservation API
        Call<List<Train>> call = iMyAPI.getTrains();
        call.enqueue(new Callback<List<Train>>() {
            @Override
            public void onResponse(Call<List<Train>> call, Response<List<Train>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(Reservations.this, response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Train> trainList = response.body();
                TrainAdapter trainAdapter = new TrainAdapter(Reservations.this,trainList,userData);
                recyclerView.setAdapter(trainAdapter);
            }

            @Override
            public void onFailure(Call<List<Train>> call, Throwable t) {
                Toast.makeText(Reservations.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}