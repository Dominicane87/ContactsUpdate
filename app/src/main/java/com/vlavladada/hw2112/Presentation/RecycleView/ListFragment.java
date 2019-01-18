package com.vlavladada.hw2112.Presentation.RecycleView;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vlavladada.hw2112.App;
import com.vlavladada.hw2112.Presentation.ContactInfo.ContactInfoFragment;
import com.vlavladada.hw2112.Data.HttpProvider;
import com.vlavladada.hw2112.Data.StoreProvider;
import com.vlavladada.hw2112.Presentation.PageView.PageView;
import com.vlavladada.hw2112.Presentation.Login.LoginFragment;
import com.vlavladada.hw2112.R;
import com.vlavladada.hw2112.model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ListFragment extends Fragment implements MyAdapter.MyClickListener {
    private MyAdapter adapter;
    private ProgressBar progressBar;
    private boolean isProgress;
    ArrayList<Contact> contacts = new ArrayList<>();
    private TextView emptyMsg;
    RecyclerView recyclerView;
    ContactInfoFragment contactInfoFragment;

    public ListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        adapter = new MyAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration divider=new DividerItemDecoration(getContext(),((LinearLayoutManager) layoutManager).getOrientation());
        recyclerView = view.findViewById(R.id.my_rv);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(divider);
        emptyMsg = view.findViewById(R.id.emptyTxt);
        progressBar = view.findViewById(R.id.progressBar);
        adapter.setListener(this);

        ItemTouchHelper helper = new ItemTouchHelper(new MyTouchCallBack());
        helper.attachToRecyclerView(recyclerView);

        return view;
    }

    class MyTouchCallBack extends ItemTouchHelper.Callback {
        int count = 0;
        Snackbar snackbar;
        ArrayList<Contact> contactsDeleted=new ArrayList<>();

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1 && snackbar != null) {
                    snackbar.dismiss();
                    count = 0;
                    snackbar = null;

                    for(Contact contact : contactsDeleted) {
                        new DeleteContactTask().execute(contact.getId());
                    }
                    contactsDeleted.clear();
                }
                return true;
            }
        });



        public MyTouchCallBack() {
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.END | ItemTouchHelper.START);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.d("MY_TAG", "onMove: ");
            adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            contactsDeleted.add(adapter.remove(viewHolder.getAdapterPosition()));
            if (snackbar == null) {
                snackbar = Snackbar.make(viewHolder.itemView, "Removed " + (++count), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                contactsDeleted.clear();
                                new LoadingList().execute();
                            }
                        });
                snackbar.show();
            } else if (snackbar.isShown()) {
                snackbar.setText("Removed " + (++count));
            }
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 3000);
        }
    }


    @Override
    public void onStart() {
        Log.d("MY", "onStart: ");
        super.onStart();
        new LoadingList().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_item && !isProgress) {
            contactInfoFragment = new ContactInfoFragment();
            Bundle args = new Bundle();
            args.putInt("MODE", ContactInfoFragment.EDIT_MODE);
            contactInfoFragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.root,contactInfoFragment)
                    .addToBackStack(null).commit();
        } else if (item.getItemId() == R.id.logout_item && !isProgress) {
            new AlertDialog.Builder(getActivity()).setMessage("Do you really want it?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new LogoutTask().execute();
                            getFragmentManager().beginTransaction().replace(R.id.root, new LoginFragment()).commit();
                        }
                    }).setNegativeButton("no!", null).setCancelable(false).create().show();
        } else if (item.getItemId() == R.id.view_page && !isProgress) {
            startActivity(new Intent(getActivity(), PageView.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Contact editcontact = contacts.get(position);
        contactInfoFragment = new ContactInfoFragment();
        Bundle args = new Bundle();
        args.putInt("MODE", ContactInfoFragment.VIEW_MODE);
        Log.d("MY", "onItemClick: "+editcontact.toString());
        Gson gson=new Gson();
        String json=gson.toJson(editcontact);
        args.putString("CONTACT", json);
        contactInfoFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.root, contactInfoFragment)
                .addToBackStack(null).commit();
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


    class DeleteContactTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            isProgress = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer...params ) {
            try {
                HttpProvider.getInstance().removeById(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isProgress = false;
            progressBar.setVisibility(View.GONE);
        }
    }


    class LogoutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            isProgress = true;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StoreProvider.getInstance().clearToken();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isProgress = false;
            progressBar.setVisibility(View.GONE);
        }
    }



    private class LoadingList extends AsyncTask<Void, Void, String> {
        ArrayList<Contact> tmp;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
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
            Log.d("MY_TAG", "onPostExecute: "+contacts.toString());
            if (s!="OK") {
                showError(s);
            } else if (!contacts.isEmpty()){
                adapter.setContacts(contacts);
                recyclerView.setVisibility(View.VISIBLE);
                emptyMsg.setVisibility(View.GONE);
            } else if (contacts==null || contacts.isEmpty()){
                    adapter.setContacts(null);
                    recyclerView.setVisibility(View.GONE);
                    emptyMsg.setVisibility(View.VISIBLE);
                }
            }
        }
    }
