package com.example.mycontactsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.mycontactsapp.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyContactsDatabase contactsDatabase;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerViewContacts;
    private ContactAdapter contactAdapter;

    private ActivityMainBinding mainBinding;
    private MainClickHandler clickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        clickHandler = new MainClickHandler(this);
        mainBinding.setButtonHandler(clickHandler);

        recyclerViewContacts = mainBinding.layoutContentMain.recyclerViewContacts;
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setHasFixedSize(true);

        contactAdapter = new ContactAdapter(contactArrayList, MainActivity.this);
        recyclerViewContacts.setAdapter(contactAdapter);



        contactsDatabase = Room.databaseBuilder(getApplicationContext(), MyContactsDatabase.class, "ContactsDB").build();

        loadContacts();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Contact contact = contactArrayList.get(viewHolder.getAdapterPosition());
                deleteContact(contact);
            }
        }).attachToRecyclerView(recyclerViewContacts);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addAndEditContact(false, null, -1);
//            }
//        });
    }

    public void addAndEditContact(final boolean isUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.add_edit_contact, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        TextView textViewContactTitle = view.findViewById(R.id.textViewContactTitle);
        final EditText editTextFirstName= view.findViewById(R.id.editTextFirstName);
        final EditText editTextLastName = view.findViewById(R.id.editTextLastName);
        final EditText editTextEmail = view.findViewById(R.id.editTextEmail);
        final EditText editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);

        textViewContactTitle.setText(!isUpdate ? "Add contact: " : "Edit contact");
        if (isUpdate && contact != null) {
            editTextFirstName.setText(contact.getFirstName());
            editTextLastName.setText(contact.getLastName());
            editTextEmail.setText(contact.getEmail());
            editTextPhoneNumber.setText(contact.getPhoneNumber());
        }
        builder.setCancelable(false).setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(editTextFirstName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextLastName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextPhoneNumber.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    if (isUpdate && contact != null) {
                        updateContact(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextEmail.getText().toString(), editTextPhoneNumber.getText().toString(), position);
                    } else {
                        addContact(editTextFirstName.getText().toString(), editTextLastName.getText().toString(), editTextEmail.getText().toString(), editTextPhoneNumber.getText().toString());
                    }
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadContacts() {
       new GetAllContactsTask().execute();
    }

    private void updateContact(String firstName, String lastName, String email, String phoneNumber, int position) {
        Contact contact = contactArrayList.get(position);
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setEmail(email);
        contact.setPhoneNumber(phoneNumber);
        new UpdateContactTask().execute(contact);
        contactArrayList.set(position, contact);
    }

    private void deleteContact(Contact contact) {
        new DeleteContactTask().execute(contact);
    }

    private void addContact(String firstName, String lastName, String email, String phoneNumber) {
        Contact contact = new Contact(0, firstName, lastName, email, phoneNumber);

        new InsertContactTask().execute(contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetAllContactsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            contactArrayList = (ArrayList<Contact>) contactsDatabase.getContactDao().getAllContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactAdapter.setContacts(contactArrayList);
        }
    }

    private class DeleteContactTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsDatabase.getContactDao().deleteContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadContacts();
        }
    }

    private class InsertContactTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsDatabase.getContactDao().insertContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadContacts();
        }
    }

    private class UpdateContactTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsDatabase.getContactDao().updateContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadContacts();
        }
    }

    public class MainClickHandler {
        Context context;

        public MainClickHandler(Context context) {
            this.context = context;
        }

        public void onClickAddAndEdit(View view) {
            addAndEditContact(false, null, -1);
        }
    }

}