package com.project.firetranslator.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.project.firetranslator.data.model.Translation;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface TranslationsDao {
    @Query("SELECT * FROM Translation")
    public LiveData<List<Translation>> getAllTranslations();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTranslation(Translation w);
    @Delete
    public void removeTranslation(Translation w);

    @Query("SELECT * FROM Translation WHERE inputText LIKE :w LIMIT 1")
    public Single<Translation> findWord(String w);
}
