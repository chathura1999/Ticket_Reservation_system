package com.example.mobile_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_app.Model.Train;
import com.example.mobile_app.Remote.IMyAPI;
import com.example.mobile_app.Model.TicketBooking;
import com.example.mobile_app.Remote.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

    List<Train> trainList;
    Context context;
    String userData;
    IMyAPI iMyAPI;
    public TrainAdapter(Context context, List<Train> trains, String userData){
        this.context = context;
        trainList = trains;
        // Initialize the userData field
        this.userData = userData;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_train,parent,false);
        return new TrainViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        String trainId;
        Train train = trainList.get(position);
        trainId = train.getId();
        holder.TrainName.setText("Train Name - "+train.getTrainName());
        holder.StartStation.setText("Start Station - "+train.getStartStation());
        holder.EndStation.setText("End Station - "+train.getEndStation());
        holder.StartTime.setText("Start Time - "+train.getStartTime());
        holder.EndTime.setText("End Time - "+ train.getEndTime());

        // Handle button click to show DatePicker
        holder.bookingDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar today = Calendar.getInstance();
                Calendar thirtyDaysLater = Calendar.getInstance();
                thirtyDaysLater.add(Calendar.DAY_OF_MONTH, 30);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);

                                // Check if the selected date is within the allowed range
                                if (selectedDate.after(today) && selectedDate.before(thirtyDaysLater)) {
                                    // Format the selected date as UTC
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    String SelectedDate = dateFormat.format(selectedDate.getTime());

                                    // Update the TextView with the selected date in UTC format
                                    holder.selectedDateTextView.setText(SelectedDate);
                                } else {
                                    // Display a message to the user that the selected date is not within the allowed range
                                    Toast.makeText(view.getContext(), "Please select a date between today and 30 days from today.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        // Set the current year, month, and day as the default selection
                        today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH),
                        today.get(Calendar.DAY_OF_MONTH)
                );

                // Set the minimum and maximum dates for the DatePickerDialog
                datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(thirtyDaysLater.getTimeInMillis());

                // Show the DatePickerDialog
                datePickerDialog.show();
            }
        });


        // Inside Make reservation on click method
        holder.SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a date has been selected

                if (holder.selectedDateTextView.getText().toString().isEmpty()) {
                    // Display a validation message if no date has been selected
                    Toast.makeText(view.getContext(), "Please select a booking date.", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform the reservation submission
                    // Extract User ID
                    if (userData != null) {
                        // Split the user data into relevant parts
                        String[] userDataParts = userData.split(",");

                        // Ensure you have proper validation and handling here based on your data structure
                        if (userDataParts.length >= 2) {

                            String userIdPart = userDataParts[0].trim();
                            String[] userIdData = userIdPart.split(":");
                            if (userIdData.length >= 2) {

                                String userIdWithQuotes = userIdData[1].trim();

                                // Remove double quotes from the Id
                                String userId = userIdWithQuotes.replaceAll("\"", "");

                                //init API
                                iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

                                // Make the POST request to your API
                                TicketBooking ticketBooking = new TicketBooking("",holder.selectedDateTextView.getText().toString(),"","","","","","Done",null);
                                Call<String> call = iMyAPI.createReservation(
                                        userId,
                                        trainId,
                                        ticketBooking
                                );

                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                        if (response.isSuccessful()) {
                                            // Handle successful API call here
                                            Toast.makeText(view.getContext(), "Reservation create successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Handle API error here
                                            if (response.errorBody() != null) {
                                                try {
                                                    // Extract the error message from the JSON response
                                                    String errorJson = response.errorBody().string();
                                                    JSONObject jsonObject = new JSONObject(errorJson);
                                                    String errorMessage = jsonObject.getString("Message");
                                                    // Display the error message in a Toast
                                                    Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Toast.makeText(view.getContext(), "Failed to create Reservation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, Throwable t) {
                                        Toast.makeText(view.getContext(), "Reservation failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }




                }
            }
        });



        // Inside Make reservation on click method
        holder.requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a date has been selected

                if (holder.selectedDateTextView.getText().toString().isEmpty()) {
                    // Display a validation message if no date has been selected
                    Toast.makeText(view.getContext(), "Please select a booking date.", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform the reservation submission
                    // Extract User ID
                    if (userData != null) {
                        // Split the user data into relevant parts
                        String[] userDataParts = userData.split(",");

                        // Ensure you have proper validation and handling here based on your data structure
                        if (userDataParts.length >= 2) {

                            String userIdPart = userDataParts[0].trim();
                            String[] userIdData = userIdPart.split(":");
                            if (userIdData.length >= 2) {

                                String userIdWithQuotes = userIdData[1].trim();

                                // Remove double quotes from the Id
                                String userId = userIdWithQuotes.replaceAll("\"", "");

                                //init API
                                iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

                                // Make the POST request to your API
                                TicketBooking ticketBooking = new TicketBooking("",holder.selectedDateTextView.getText().toString(),"","","","","","Pending",null);
                                Call<String> call = iMyAPI.createReservation(
                                        userId,
                                        trainId,
                                        ticketBooking
                                );

                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                        if (response.isSuccessful()) {
                                            // Handle successful API call here
                                            Toast.makeText(view.getContext(), "Reservation request send successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Handle API error here
                                            if (response.errorBody() != null) {
                                                try {
                                                    // Extract the error message from the JSON response
                                                    String errorJson = response.errorBody().string();
                                                    JSONObject jsonObject = new JSONObject(errorJson);
                                                    String errorMessage = jsonObject.getString("Message");
                                                    // Display the error message in a Toast
                                                    Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Toast.makeText(view.getContext(), "Failed to create Reservation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, Throwable t) {
                                        Toast.makeText(view.getContext(), "Reservation failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }




                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return trainList.size();
    }

    public class  TrainViewHolder extends RecyclerView.ViewHolder{
        TextView TrainName,StartStation,EndStation,StartTime,EndTime;
        DatePicker datePicker;

        TextView selectedDateTextView;
        Button bookingDateButton, SubmitButton, requestbtn;


        public TrainViewHolder(@NonNull View itemView) {
            super(itemView);


            TrainName = itemView.findViewById(R.id.txtTrainName);
            StartStation = itemView.findViewById(R.id.txtStartStation);
            EndStation = itemView.findViewById(R.id.txtEndStation);
            StartTime = itemView.findViewById(R.id.txtStartTime);
            EndTime = itemView.findViewById(R.id.txtEndTime);
            bookingDateButton = itemView.findViewById(R.id.bookingDateButton);
            datePicker = itemView.findViewById(R.id.datePicker);
            selectedDateTextView = itemView.findViewById(R.id.selectedDateTextView);
            SubmitButton = itemView.findViewById(R.id.SubmitButton);
            requestbtn = itemView.findViewById(R.id.requestbtn);
        }
    }
}
