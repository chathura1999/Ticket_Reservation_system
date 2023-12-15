package com.example.mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_app.Model.tblUser;
import com.example.mobile_app.Remote.IMyAPI;
import com.example.mobile_app.Remote.RetrofitClient;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    IMyAPI iMyAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextView textViewUsername;
    Button btnLogout, btnDeactivateAccount;
    private boolean userLoggedIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        textViewUsername = findViewById(R.id.textViewUsername);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeactivateAccount = findViewById(R.id.btnDeactivateAccount);
        Button btnMakeReservation = findViewById(R.id.btnMakeReservation);
        Button btnMyBookings = findViewById(R.id.btnMyBookings);

        // Get user data passed from MainActivity
        Intent intent = getIntent();
        String userData = intent.getStringExtra("userData");

        // Extract and display user information
        if (userData != null) {
            // Split the user data into relevant parts (modify this based on your actual data structure)
            String[] userDataParts = userData.split(",");

            // Ensure you have proper validation and handling here based on your data structure
            if (userDataParts.length >= 2) {
                String usernamePart = userDataParts[1].trim();
                String[] usernameData = usernamePart.split(":");
                String userIdPart = userDataParts[0].trim();
                String[] userIdData = userIdPart.split(":");
                if (usernameData.length >= 2 || userIdData.length >= 2) {
                    String usernameWithQuotes = usernameData[1].trim();
                    String userIdWithQuotes = usernameData[1].trim();

                    // Remove double quotes from the username
                    String username = usernameWithQuotes.replaceAll("\"", "");
                    String id = userIdWithQuotes.replaceAll("\"", "");
                    textViewUsername.setText(username);
                }
            }
        }

        // Set a click listener for the "Make Reservation" button
        btnMakeReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the ReservationsActivity
                Intent reservationIntent = new Intent(ProfileActivity.this, Reservations.class);
                reservationIntent.putExtra("userData", userData);
                // Start the ReservationsActivity
                startActivity(reservationIntent);
            }
        });

        // Set a click listener for the "My Bookings" button
        btnMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the MyBookings
                Intent reservationIntent = new Intent(ProfileActivity.this, MyBookings.class);
                reservationIntent.putExtra("userData", userData);
                // Start the MyBookings
                startActivity(reservationIntent);
            }
        });

        // Logout button click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the MainActivity
                Intent logoutIntent = new Intent(ProfileActivity.this, MainActivity.class);
                // Start the MainActivity and finish the ProfileActivity
                startActivity(logoutIntent);
                finish();
            }
        });

        // Deactivate Account button click listener
        btnDeactivateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setMessage("Are you sure you want to deactivate your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // user id get
                                Intent intent = getIntent();
                                String userData = intent.getStringExtra("userData");

                                // Extract and display user information
                                String id = null;
                                if (userData != null) {
                                    // Split the user data into relevant parts (modify this based on your actual data structure)
                                    String[] userDataParts = userData.split(",");

                                    // Ensure you have proper validation and handling here based on your data structure
                                    if (userDataParts.length >= 2) {

                                        String userIdPart = userDataParts[0].trim();
                                        String[] userIdData = userIdPart.split(":");
                                        if (userIdData.length >= 2) {
                                            String userIdWithQuotes = userIdData[1].trim();
                                            // Remove double quotes from the username
                                            id = userIdWithQuotes.replaceAll("\"", "");
                                            //Create user to login
                                            tblUser user = new tblUser("", "",
                                                    "", "", "", false,new ArrayList<>());
                                        }
                                    }
                                }

                                // Create a Retrofit instance for making the PUT request
                                iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

                                // Create a tblUser object with the necessary data to deactivate the account
                                tblUser userToDeactivate = new tblUser();
                                userToDeactivate.setId(id);

                                // Make the PUT request to deactivate the account
                                compositeDisposable.add(iMyAPI.deactivateUser(id)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String response) throws Exception {
                                                // Handle a successful deactivation response
                                                Toast.makeText(ProfileActivity.this, "Account deactivated successfully.", Toast.LENGTH_SHORT).show();

                                                // Redirect the user to the login screen or perform other necessary actions
                                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish(); // Close the current activity
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                // Handle the error case
                                                Toast.makeText(ProfileActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                );

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }

    @Override
    public void onBackPressed() {
        if (userLoggedIn) {
            // If the user is logged in, do nothing when the back button is pressed
        } else {
            // Allow normal back navigation for logged-out users
            super.onBackPressed();
        }
    }
}