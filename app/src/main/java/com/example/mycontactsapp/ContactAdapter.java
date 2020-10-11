package com.example.mycontactsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycontactsapp.databinding.ContactListItemBinding;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<Contact> contacts = new ArrayList<>();
    private MainActivity mainActivity;

    public ContactAdapter (ArrayList<Contact> contactArrayList, MainActivity mainActivity){
        this.contacts = contactArrayList;
        this.mainActivity = mainActivity;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
//        return new ContactViewHolder(itemView);
        ContactListItemBinding listItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, final int position) {
        final Contact contact = contacts.get(position);
//        holder.textViewLastName.setText(contact.getLastName());
//        holder.textViewFirstName.setText(contact.getFirstName());
//        holder.textViewEmail.setText(contact.getEmail());
//        holder.textViewPhoneNumber.setText(contact.getPhoneNumber());

        holder.listItemBinding.setContact(contact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addAndEditContact(true, contact, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

//        private TextView textViewFirstName;
//        private TextView textViewLastName;
//        private TextView textViewEmail;
//        private TextView textViewPhoneNumber;
        private ContactListItemBinding listItemBinding;

        public ContactViewHolder(@NonNull ContactListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            this.listItemBinding = listItemBinding;
//            textViewFirstName = itemView.findViewById(R.id.textViewFirstName);
//            textViewLastName = itemView.findViewById(R.id.textViewLastName);
//            textViewEmail = itemView.findViewById(R.id.textViewEmail);
//            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
        }
    }

}
