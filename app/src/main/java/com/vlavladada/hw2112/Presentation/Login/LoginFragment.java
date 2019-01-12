package com.vlavladada.hw2112.Presentation.Login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.vlavladada.hw2112.Data.HttpProvider;
import com.vlavladada.hw2112.Data.StoreProvider;
import com.vlavladada.hw2112.Presentation.RecycleView.ListFragment;
import com.vlavladada.hw2112.R;
import com.vlavladada.hw2112.model.EmailValidationException;
import com.vlavladada.hw2112.model.PasswordValidationException;

import java.io.IOException;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private ProgressBar myProgress;
    private EditText inputEmail, inputPassword;
    private Button regBtn, loginBtn;

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        myProgress = view.findViewById(R.id.myProgress);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.inputPassword);
        regBtn = view.findViewById(R.id.regBtn);
        loginBtn = view.findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.regBtn){
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            try {
                checkEmail(email);
                checkPassword(password);
                new RegistrationTask(email,password).execute();
            }catch (EmailValidationException e){
                showEmailError(e.getMessage());
            }catch (PasswordValidationException e){
                showPasswordError(e.getMessage());
            }
        } else if (v.getId()==R.id.loginBtn){
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            try{
                checkEmail(email);
                checkPassword(password);
                showProgress();
                new LoginTask(email,password).execute();
            }catch (EmailValidationException e){
                showEmailError(e.getMessage());
                inputEmail.setError(e.getMessage());
            }catch (PasswordValidationException e){
                inputPassword.setError(e.getMessage());
                showPasswordError(e.getMessage());
            }
        }
    }

    private void showProgress(){
        myProgress.setVisibility(View.VISIBLE);
        inputEmail.setVisibility(View.INVISIBLE);
        inputPassword.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);
        regBtn.setVisibility(View.INVISIBLE);
    }

    private void hideProgress(){
        myProgress.setVisibility(View.GONE);
        inputEmail.setVisibility(View.VISIBLE);
        inputPassword.setVisibility(View.VISIBLE);
        regBtn.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
    }

    private void showEmailError(String error){
        inputEmail.setError(error);
    }

    private void showPasswordError(String error){
        inputPassword.setError(error);
    }

    private void showError(String error) {
        new AlertDialog.Builder(this.getActivity())
                .setMessage(error)
                .setTitle("Error!")
                .setPositiveButton("Ok", null)
                .setCancelable(false)
                .create()
                .show();
    }

    private void showNextFragment(){
        ListFragment listFragment = new ListFragment();
        getFragmentManager().beginTransaction().replace(R.id.root, listFragment).commit();
    }


    private void checkEmail(String email) throws EmailValidationException {
        if(email.isEmpty()){
            throw new EmailValidationException("Email can't be empty!");
        }

        int at = email.indexOf("@");
        if(at < 0) {
            throw new EmailValidationException("Wrong email format! Example: name@mail.com");
        }

        if(email.lastIndexOf("@") != at) {
            throw new EmailValidationException("Wrong email format! Example: name@mail.com");
        }

        int dot = email.lastIndexOf(".");
        if(dot < 0 || dot < at) {
            throw new EmailValidationException("Wrong email format! Example: name@mail.com");
        }

        if(email.length() - 1 - dot <= 1) {
            throw new EmailValidationException("Wrong email format! Example: name@mail.com");
        }
    }

    private void checkPassword(String password) throws PasswordValidationException {
        if(password.length() < 8) {
            throw new PasswordValidationException("Password length need be 8 or more symbols");
        }
        boolean[] tests = new boolean[4];
        char[] arr = password.toCharArray();
        for (char anArr : arr) {
            if (Character.isUpperCase(anArr)) {
                tests[0] = true;
            }

            if (Character.isLowerCase(anArr)) {
                tests[1] = true;
            }

            if (Character.isDigit(anArr)) {
                tests[2] = true;
            }

            if (isSpecSymbol(anArr)) {
                tests[3] = true;
            }
        }

        if(!tests[0]){
            throw new PasswordValidationException("Password must contain at least one uppercase letter!");
        }
        if(!tests[1]){
            throw new PasswordValidationException("Password must contain at least one lowercase letter!");
        }
        if(!tests[2]){
            throw new PasswordValidationException("Password must contain at least one digit!");
        }
        if(!tests[3]){
            throw new PasswordValidationException("Password must contain at least one special symbol from ['$','~','-','_']!");
        }
    }

    private boolean isSpecSymbol(char c) {
        char[] arr = {'$','~','-','_'};
        for (char anArr : arr) {
            if (anArr == c) {
                return true;
            }
        }
        return false;
    }


    private class RegistrationTask extends AsyncTask<Void,Void,String> {
        private String email, password;
        private boolean isSuccess = true;

        public RegistrationTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Registration OK";
            try {
                String token = HttpProvider.getInstance().registration(email,password);
                StoreProvider.getInstance().saveToken(token);
            } catch (IOException e){
                e.printStackTrace();
                result = "Connection error!Check your internet!";
                isSuccess = false;
            }catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                isSuccess = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            hideProgress();
            if (isSuccess){
                showNextFragment();
            }else{
                Log.d("TAG", "reg onPostExecute: "+s);
                showError(s);
            }
        }
    }

    private class LoginTask extends AsyncTask<Void,Void,String>{
        private String email, password;
        private boolean isSuccess = true;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Login OK";
            try {
                String token = HttpProvider.getInstance().login(email,password);
                StoreProvider.getInstance().saveToken(token);
            } catch (IOException e){
                e.printStackTrace();
                result = "Connection error! Check your internet!";
                isSuccess = false;
            }catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                Log.d("TAG", "log onPostExecute: "+result);
                isSuccess = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            hideProgress();
            if (isSuccess){
                showNextFragment();
            }else{
                showError(s);
            }
        }
    }


    public static class MainActivity extends AppCompatActivity {
        private LoginFragment loginFragment;
        private ListFragment listFragment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            loginFragment = new LoginFragment();
            listFragment = new ListFragment();
            if (isLogined()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.root, listFragment).commit();
            } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.root, loginFragment).commit();
            }
        }

        private boolean isLogined() {
            if (StoreProvider.getInstance().getToken()!=null){
                return true;
            }
            return false;
        }


    }
}

