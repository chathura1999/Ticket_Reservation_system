package com.example.mobile_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_app.Model.TicketBooking;
import com.example.mobile_app.Remote.IMyAPI;
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

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    Context context;
    List<TicketBooking> bookingList;
    IMyAPI iMyAPI;
    public BookingAdapter(Context context, List<TicketBooking> bookingList){
        this.context = context;
        this.bookingList = bookingList;
    }
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_my_bookings,parent,false);
        return new BookingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {

        TicketBooking ticketBooking = bookingList.get(position);

        holder.TrainName2.setText("Train Name - "+ticketBooking.getTrainName());
        holder.ReservationDate2.setText("Ticket Reservation Date - "+ticketBooking.getReservationDate());
        holder.UserNIC2.setText("NIC - "+ticketBooking.getUserNIC());
        holder.txtBookingStatus.setText("Booking Status - "+ticketBooking.getBookingStatus());

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


        // Handle button click to Update the date
        holder.UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a date has been selected
                if (holder.selectedDateTextView.getText().toString().isEmpty()) {
                    Toast.makeText(view.getContext(), "Please select a booking date.", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform the reservation submission
                    //init API
                    iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

                    String id = ticketBooking.getId().toString();
                    String newReservationDate  = holder.selectedDateTextView.getText().toString();

                    //call updateReservationDate API
                    Call<Void> call = iMyAPI.updateReservationDate(id,newReservationDate );

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Handle successful API call here
                                Toast.makeText(view.getContext(), "ReservationDate updated successfully", Toast.LENGTH_SHORT).show();
                                ticketBooking.setReservationDate(newReservationDate);
                                notifyItemChanged(position);
                                holder.selectedDateTextView.setText("New selected date"+"");
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
                                    Toast.makeText(view.getContext(), "Failed to Update ReservationDate", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(view.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            }
        });


        // Handle button click to Cancel the reservation
        holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to Cancel reservation?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //init API
                                iMyAPI = RetrofitClient.getInstance().create(IMyAPI.class);

                                String reservationId = ticketBooking.getId();

                                Call<Void> call = iMyAPI.cancelReservation(reservationId);

                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            // Reservation canceled successfully
                                            Toast.makeText(view.getContext(), "Reservation cancel successfully", Toast.LENGTH_SHORT).show();
                                            bookingList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, bookingList.size());
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
                                                // Handle other types of errors if needed
                                                Toast.makeText(view.getContext(), "Failed to cancel Reservation", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        // Network or other errors
                                        Toast.makeText(view.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


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
    public int getItemCount() {
        return bookingList.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder{
        TextView TrainName2,UserNIC2,ReservationDate2,txtBookingStatus;
        TextView selectedDateTextView;
        Button bookingDateButton,UpdateButton,DeleteButton;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            TrainName2 = itemView.findViewById(R.id.txtTrainName2);
            UserNIC2 = itemView.findViewById(R.id.txtUserNIC2);
            ReservationDate2 = itemView.findViewById(R.id.txtReservationDate2);
            bookingDateButton = itemView.findViewById(R.id.bookingDateButton);
            selectedDateTextView = itemView.findViewById(R.id.selectedDateTextView);
            UpdateButton = itemView.findViewById(R.id.UpdateButton);
            DeleteButton = itemView.findViewById(R.id.DeleteButton);
            txtBookingStatus = itemView.findViewById(R.id.txtBookingStatus);
        }
    }
}
