package com.vlavladada.hw2112.Data;

import android.util.Log;

import com.google.gson.Gson;
import com.vlavladada.hw2112.model.AuthDto;
import com.vlavladada.hw2112.model.AuthResponseDto;
import com.vlavladada.hw2112.model.Contact;
import com.vlavladada.hw2112.model.ContactListDto;
import com.vlavladada.hw2112.model.ErrorDto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpProvider {
    private Api api;
    private static final String BASE_URL = "https://contacts-telran.herokuapp.com/";
    private static final HttpProvider ourInstance = new HttpProvider();

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    private HttpProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    public String registration(String email, String password) throws Exception {

        AuthDto auth = new AuthDto(email, password);
        Call<AuthResponseDto> call = api.registration(auth);
        retrofit2.Response<AuthResponseDto> response = call.execute();

        if (response.isSuccessful()) {
            AuthResponseDto token = response.body();
            Log.d("MY_TAG", "registration: " + token.getToken());
            return token.getToken();
        } else  {
            String json = response.errorBody().string();
            Log.d("MY_TAG", "registration: " + json);
            throw new Exception(json);
        }
    }


    public String login(String email, String password) throws Exception {

        AuthDto auth = new AuthDto(email,password);
        Call<AuthResponseDto> call = api.login(auth);
        retrofit2.Response<AuthResponseDto> response = call.execute();
        if(response.isSuccessful()){
            AuthResponseDto token = response.body();
            Log.d("MY_TAG", "registration: " + token.getToken());
            return token.getToken();
        }else if(response.code() == 401){
            throw new Exception("Wrong email or password!");
        } else {
            throw new Exception("Server error!");
        }
    }

    public ArrayList<Contact> getContactList() throws Exception {
        String token = StoreProvider.getInstance().getToken();
        if (token != null) {
            Call<ContactListDto> call=api.getAllContacts(token);
            retrofit2.Response<ContactListDto> response = call.execute();
            if(response.isSuccessful()){
                ContactListDto contacts = response.body();
                return contacts.getContacts();
            }else{
                Log.d("MY_TAG", "run: " + response.errorBody().string());
                throw new Exception(response.errorBody().string());
            }
        }
        throw new Exception("Empty list!");
    }

    public long addContact(Contact contact) throws Exception {
        String token = StoreProvider.getInstance().getToken();
        if (token != null) {
            Call<Contact> call=api.addContact(token,contact);
            retrofit2.Response<Contact> response=call.execute();

            if (response.isSuccessful()) {
                return response.body().getId();
            } else if (response.code() == 409) {
                throw new Exception("Duplicate contact fields! Email and phone need be unique to each contact");
            } else {
                Log.d("MY_TAG", "addingContact error: " + response.errorBody().string());
                throw new Exception(response.errorBody().string());
            }
        } else {
            throw new Exception("token is null!");
        }
    }

    public boolean updateContact(Contact contact) throws Exception {
        String token = StoreProvider.getInstance().getToken();
        if (token != null) {
            Call<Contact> call=api.updateContact(token,contact);
            retrofit2.Response<Contact> response=call.execute();

            if (response.isSuccessful()) {
                return true;
            } else {
                Log.d("MY_TAG", "updating error: " + response.errorBody().string());
                throw new Exception(response.errorBody().string());
            }
        } else {
            throw new Exception("token is null!");
        }
    }

    public boolean removeById(int id) throws Exception {
        String token = StoreProvider.getInstance().getToken();
        if (token != null) {

            Call<Void> call=api.deleteContact(token,(long) id);
            retrofit2.Response<Void> response=call.execute();

            if (response.isSuccessful()) {
                return true;
            } else {
                Log.d("MY_TAG", "removeById error: " + response.errorBody().string());
                throw new Exception(response.errorBody().string());
            }
        } else {
            throw new Exception("token is null!");
        }
    }
}
