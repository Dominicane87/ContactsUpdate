package com.vlavladada.hw2112.Presentation.RecycleView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vlavladada.hw2112.Data.HttpProvider;
import com.vlavladada.hw2112.R;
import com.vlavladada.hw2112.model.Contact;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Contact> contacts;
    private MyClickListener listener;

    public MyAdapter(){
        try {
            this.contacts = HttpProvider.getInstance().getContactList();
        } catch (Exception e) {
            this.contacts=null;
        }

    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }
    public ArrayList<Contact> getContacts(){
        return contacts;
    }

    public Contact remove(int pos){
        Contact contact = contacts.remove(pos);
        notifyItemRemoved(pos);
        return contact;
    }
    public  void add(int pos, Contact contact){
        contacts.add(pos, contact);
        notifyItemInserted(pos);
    }
    public  void move(int from, int to){
        Contact contact=contacts.remove(from);
        contacts.add(to, contact);
        notifyItemMoved(from,to);
    }

    public void setListener(MyClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_view, viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Contact contact = contacts.get(i);
        myViewHolder.nameTxt.setText(contact.getName());
        myViewHolder.phoneTxt.setText(contact.getPhone());

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt, phoneTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_inlist);
            phoneTxt = itemView.findViewById(R.id.phone_inlist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface MyClickListener{
        void onItemClick(int position);
    }
}
