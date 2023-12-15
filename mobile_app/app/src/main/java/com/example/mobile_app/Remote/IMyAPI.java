package com.example.mobile_app.Remote;

import com.example.mobile_app.Model.TicketBooking;
import com.example.mobile_app.Model.Train;
import com.example.mobile_app.Model.tblUser;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMyAPI {
    // Add the POST request to user registration
    @POST("api/users")
    Observable<String> registerUser(@Body tblUser user);

    // Add the POST request to user login
    @POST("login")
    Observable<String> loginUser(@Body tblUser user);

    // Add the PUT request to deactivate a user
    @PUT("api/users/mobile/user/deactivate/{id}")
    Observable<String> deactivateUser(@Path("id") String userId);

    // Add the GET request to get train reservations
    @GET("api/train")
    Call<List<Train>> getTrains();

    // Add the POST request to make train reservations
    @POST("mobile/addReservation")
    Call<String> createReservation(
            @Query("userId") String userId,
            @Query("trainId") String trainId,
            @Body TicketBooking ticketBooking
    );

    // Add the GET request to get user bookings
    @GET("api/train/{userId}")
    Call<List<TicketBooking>> getUserBookings(@Path("userId") String userId);

    // Add the PUT request to get update user bookings
    @PUT("api/train/update-reservation-date")
    Call<Void> updateReservationDate(@Query("id") String id, @Query("newReservationDate") String newReservationDate);

    // Add the DELETE request to get delete user bookings
    @DELETE("api/train/{reservationId}")
    Call<Void> cancelReservation(@Path("reservationId") String reservationId);
}
