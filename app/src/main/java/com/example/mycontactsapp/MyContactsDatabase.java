package com.example.mycontactsapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 1, exportSchema = false)
public abstract class MyContactsDatabase extends RoomDatabase {

    public abstract ContactDAO getContactDao();

}
