package com.example.mycontactsapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDAO {

    @Insert
    void insertContact(Contact contact);

    @Query("SELECT * FROM contacts_table ")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM contacts_table WHERE contactId == :contactId")
    Contact getContact(long contactId);

    @Update
    void updateContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);
}
