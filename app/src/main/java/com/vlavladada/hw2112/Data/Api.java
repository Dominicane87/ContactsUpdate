package com.vlavladada.hw2112.Data;

import com.vlavladada.hw2112.model.AuthDto;
import com.vlavladada.hw2112.model.AuthResponseDto;
import com.vlavladada.hw2112.model.Contact;
import com.vlavladada.hw2112.model.ContactListDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @POST("api/registration")
    Call<AuthResponseDto> registration(@Body AuthDto auth);

    @POST("api/login")
    Call<AuthResponseDto> login(@Body AuthDto auth);

    @GET("api/contact")
    Call<ContactListDto> getAllContacts(@Header("Authorization") String token);

    @POST("api/contact")
    Call<Contact> addContact(@Header("Authorization") String token, @Body Contact contact);

    @PUT("api/contact")
    Call<Contact> updateContact(@Header("Authorization") String token, @Body Contact contact);

    @DELETE("api/contact/{id}")
    Call<Void> deleteContact(@Header("Authorization") String token, @Path("id") long id);

}
