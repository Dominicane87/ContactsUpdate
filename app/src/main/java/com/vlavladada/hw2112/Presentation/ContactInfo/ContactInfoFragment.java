package com.vlavladada.hw2112.Presentation.ContactInfo;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vlavladada.hw2112.Data.HttpProvider;
import com.vlavladada.hw2112.R;
import com.vlavladada.hw2112.model.Contact;


public class ContactInfoFragment extends Fragment {
    private ViewGroup editWrapper, textWrapper, progressFrame;
    private TextView nameTxt, lastNameTxt, emailTxt, phoneTxt, addressTxt, descTxt;
    private EditText inputName, inputLastName, inputEmail, inputPhone, inputAddress, inputDesc;
    private MenuItem doneItem, deleteItem, editItem;
    private boolean isProgress = false;
    private Contact curr;
    private int count;
    private Callback callback;
    private boolean pageView = true;
    public static final int VIEW_MODE = 0x01;
    public static final int EDIT_MODE = 0x02;
    private int id;
    private int mode;


    public ContactInfoFragment() {

    }

    public static ContactInfoFragment newInstance (Contact contact, Callback callback, int count){
        ContactInfoFragment contactInfoFragment = new ContactInfoFragment();
        contactInfoFragment.curr = contact;
        contactInfoFragment.mode = VIEW_MODE;
        contactInfoFragment.id = contact.getId();
        contactInfoFragment.callback = callback;
        contactInfoFragment.count=count;
        return contactInfoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_info, container, false);
        editWrapper = view.findViewById(R.id.editWrapper);
        textWrapper = view.findViewById(R.id.textWrapper);
        progressFrame = view.findViewById(R.id.progressFrame);
        nameTxt = view.findViewById(R.id.nameTxt);
        lastNameTxt = view.findViewById(R.id.lastNameTxt);
        emailTxt = view.findViewById(R.id.emailTxt);
        phoneTxt = view.findViewById(R.id.phoneTxt);
        addressTxt = view.findViewById(R.id.addressTxt);
        descTxt = view.findViewById(R.id.descTxt);
        inputName = view.findViewById(R.id.inputName);
        inputLastName = view.findViewById(R.id.inputLastName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPhone = view.findViewById(R.id.inputPhone);
        inputAddress = view.findViewById(R.id.inputAddress);
        inputDesc = view.findViewById(R.id.inputDesc);
        progressFrame.setOnClickListener(null);
        Bundle args = getArguments();
        if (args!=null) {
            pageView = false;
            mode = args.getInt("MODE");
            Log.d("MY", "onCreateView: "+mode);
            if (mode == VIEW_MODE) {
                String tmp = args.getString("CONTACT");
                Gson gson=new Gson();
                Log.d("MY", "onCreateView: "+tmp);
                curr = gson.fromJson(tmp,Contact.class);
                id = curr.getId();
            } else {
                curr = new Contact("", "", "", "", "", "", 0);
                id = 0;
            }
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sd_contact_menu, menu);
        doneItem = menu.findItem(R.id.save_item);
        editItem = menu.findItem(R.id.edit_item);
        deleteItem = menu.findItem(R.id.delete_item);
        if (mode == EDIT_MODE) {
            getCurrentData();
            showEditMode();
        } else {
            setCurrentData();
            showViewMode();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_item && !isProgress) {
            if (getCurrentData()) {
                saveCurrent();
            }
        } else if (item.getItemId() == R.id.edit_item && !isProgress) {
            setCurrentData();
            showEditMode();
        } else if (item.getItemId() == R.id.delete_item && !isProgress) {
            if (id!=0) {
                deleteCurrent();
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCurrent() {
        new RemoveTask().execute();
    }

    private void saveCurrent() {
        getCurrentData();
        if (curr.getId()==0) {
            new AddTask().execute();
        } else {
            new UpdateTask().execute();
        }
    }

    private void showEditMode() {
        editWrapper.setVisibility(View.VISIBLE);
        textWrapper.setVisibility(View.GONE);
        deleteItem.setVisible(true);
        doneItem.setVisible(true);
        editItem.setVisible(false);
    }

    private void showViewMode() {
        editWrapper.setVisibility(View.GONE);
        textWrapper.setVisibility(View.VISIBLE);
        deleteItem.setVisible(false);
        doneItem.setVisible(false);
        editItem.setVisible(true);
    }

    private void setCurrentData() {
        nameTxt.setText(curr.getName());
        lastNameTxt.setText(curr.getLastName());
        emailTxt.setText(curr.getEmail());
        phoneTxt.setText(curr.getPhone());
        addressTxt.setText(curr.getAddress());
        descTxt.setText(curr.getDescription());
        inputName.setText(curr.getName());
        inputLastName.setText(curr.getLastName());
        inputEmail.setText(curr.getEmail());
        inputPhone.setText(curr.getPhone());
        inputAddress.setText(curr.getAddress());
        inputDesc.setText(curr.getDescription());
    }

    private boolean getCurrentData() {
        boolean res = false;
        String name = inputName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String email = inputEmail.getText().toString();
        String phone = inputPhone.getText().toString();
        String address = inputAddress.getText().toString();
        String desc = inputDesc.getText().toString();

        Contact tmp = new Contact(name, lastName, email, phone, address, desc, id);
        if (mode == EDIT_MODE && id == 0) {
            curr = tmp;
            curr.setId(id);
            res = true;
        } else {
            if (!valid(tmp)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Invalidate data!")
                        .setMessage("All fields need be filled!")
                        .setPositiveButton("Ok", null)
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                curr = tmp;
                res = true;
            }
        }
        return res;
    }

    private boolean valid(Contact tmp) {
        return !tmp.getName().trim().isEmpty()
                && !tmp.getLastName().trim().isEmpty()
                && !tmp.getEmail().trim().isEmpty()
                && !tmp.getPhone().trim().isEmpty()
                && !tmp.getAddress().trim().isEmpty()
                && !tmp.getDescription().trim().isEmpty();
    }

    private void showError(String error) {
        new AlertDialog.Builder(getActivity())
                .setMessage(error)
                .setTitle("Error!")
                .setPositiveButton("Ok", null)
                .setCancelable(false)
                .create()
                .show();
    }

    private class AddTask extends AsyncTask<Void, Void, Void> {
        private Contact addedContact;
        private String res;
        private boolean success;

        @Override
        protected void onPreExecute() {
            isProgress = true;
            progressFrame.setVisibility(View.VISIBLE);
            addedContact = curr;
            success = true;
            res = "OK";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpProvider.getInstance().addContact(addedContact);
            } catch (Exception e) {
                success = false;
                res = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isProgress = false;
            progressFrame.setVisibility(View.GONE);
            if (success) {
                setCurrentData();
                showViewMode();
            } else {
                showError(res);
            }
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {
        private Contact updatedContact;
        private String res;
        private boolean success;

        @Override
        protected void onPreExecute() {
            isProgress = true;
            progressFrame.setVisibility(View.VISIBLE);
            updatedContact = curr;
            success = true;
            res = "OK";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpProvider.getInstance().updateContact(updatedContact);
            } catch (Exception e) {
                success = false;
                res = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isProgress = false;
            progressFrame.setVisibility(View.GONE);
            if (success) {
                setCurrentData();
                showViewMode();
            } else {
                showError(res);
            }
        }
    }

    private class RemoveTask extends AsyncTask<Void, Void, Void> {
        private int remId;
        private String res;
        private boolean success;

        @Override
        protected void onPreExecute() {
            isProgress = true;
            progressFrame.setVisibility(View.VISIBLE);
            remId = id;
            success = true;
            res = "OK";
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                HttpProvider.getInstance().removeById(remId);
            } catch (Exception e) {
                success = false;
                res = e.getMessage();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isProgress = false;
            progressFrame.setVisibility(View.GONE);
            if (success) {

                showViewMode();

                if (callback != null) {
                    callback.onRemoveClick();
                }
                getFragmentManager().popBackStack();
//                if (pageView) {
//                    getActivity().finish();
//
//                }
            }
                else {
                    showError(res);
                }
            }
        }


        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        public interface Callback {
            void onRemoveClick();
//        void emptyList();
        }


}
