package com.project.firetranslator.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.project.firetranslator.data.model.Translation;

@Database(entities = {Translation.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TranslationsDao translationsDao();
    private static AppDatabase instance;
    public static AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "translateApp").build();
        }
        return instance;
    }
}
