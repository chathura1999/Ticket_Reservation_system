package com.example.mobile_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mobile_app.Remote.IMyAPI;
import com.example.mobile_app.Model.TicketBooking;
import com.example.mobile_app.Remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookings extends AppCompatActivity {

    private RecyclerView recyclerView;
    IMyAPI iMyAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        Intent intent = getIntent();
        String userData = intent.getStringExtra("userData");

        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Extract and display user information
        if (userData != null) {
            // Split the user data into relevant parts
            String[] userDataParts = userData.split(",");

            // Ensure you have proper validation and handling here based on your data structure
            if (userDataParts.length >= 2) {

                String userIdPart = userDataParts[0].trim();
                String[] userIdData = userIdPart.split(":");
                if (userIdData.length >= 2) {

                    String userIdWithQuotes = userIdData[1].trim();

                    String userId = userIdWithQuotes.replaceAll("\"", "");

                    //init API
                    iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

                    Call<List<TicketBooking>> call = iMyAPI.getUserBookings(userId);

                    call.enqueue(new Callback<List<TicketBooking>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<TicketBooking>> call, @NonNull Response<List<TicketBooking>> response) {
                            if(!response.isSuccessful()){
                                Toast.makeText(MyBookings.this, response.code(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            List<TicketBooking> ticketBookings = response.body();
                            BookingAdapter bookingAdapter = new BookingAdapter(MyBookings.this,ticketBookings);
                            recyclerView.setAdapter(bookingAdapter);
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<TicketBooking>> call, Throwable t) {
                            Toast.makeText(MyBookings.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }

    }
}