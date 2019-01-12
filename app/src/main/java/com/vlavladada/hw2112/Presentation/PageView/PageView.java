package com.vlavladada.hw2112.Presentation.PageView;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vlavladada.hw2112.Presentation.ContactInfo.ContactInfoFragment;
import com.vlavladada.hw2112.Data.HttpProvider;
import com.vlavladada.hw2112.R;
import com.vlavladada.hw2112.model.Contact;

import java.util.ArrayList;

public class PageView extends AppCompatActivity implements ContactInfoFragment.Callback{
    private ViewPager myPager;
    private ProgressBar progressBar;
    ArrayList<Contact> contacts = new ArrayList<>();
    private TextView emptyMsg;
    PageViewAdapter pageViewAdapter;
    String error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);
        emptyMsg = findViewById(R.id.emptyTxt);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        myPager = findViewById(R.id.my_view_pager);

        contacts=new ArrayList<>();
        pageViewAdapter=new PageViewAdapter(getSupportFragmentManager());
        pageViewAdapter.setContacts(contacts);
        myPager.setAdapter(pageViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadingList().execute();
    }

    private void showError(String error) {
        new AlertDialog.Builder(this)
                .setMessage(error)
                .setTitle("Error!")
                .setPositiveButton("Ok", null)
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onRemoveClick() {
        pageViewAdapter.onRemoveClick();

    }

    class PageViewAdapter extends FragmentStatePagerAdapter implements ContactInfoFragment.Callback {
        ArrayList<Contact> contacts;
        public PageViewAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setContacts(ArrayList<Contact> contacts) {
            this.contacts = contacts;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int i) {
            return ContactInfoFragment.newInstance(contacts.get(i), this, i);
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public void onRemoveClick() {
            notifyDataSetChanged();
            new LoadingList().execute();
       }
    }

    private class LoadingList extends AsyncTask<Void, Void, String> {

        ArrayList<Contact> tmp;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            myPager.setVisibility(View.GONE);
            emptyMsg.setVisibility(View.GONE);
            tmp = new ArrayList<>();

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                tmp = HttpProvider.getInstance().getContactList();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            contacts=tmp;
            error=s;

            if (s!="OK"){
                showError(s);
            } else if (!contacts.isEmpty()){
                myPager.setVisibility(View.VISIBLE);
                emptyMsg.setVisibility(View.GONE);
                pageViewAdapter.setContacts(contacts);
            } else if (contacts==null||contacts.isEmpty()){
                myPager.setVisibility(View.GONE);
                emptyMsg.setVisibility(View.VISIBLE);
            }
        }
    }
}
