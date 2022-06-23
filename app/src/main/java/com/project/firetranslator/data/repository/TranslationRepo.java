package com.project.firetranslator.data.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.view.animation.TranslateAnimation;

import androidx.lifecycle.LiveData;

import com.project.firetranslator.data.local.AppDatabase;
import com.project.firetranslator.data.local.TranslationsDao;
import com.project.firetranslator.data.model.Translation;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class TranslationRepo {
    private TranslationsDao mDao;
    private LiveData<List<Translation>> mSavedT;
    private static TranslationRepo instance;
    private static final String TAG = "TranslationRepo";
    public static TranslationRepo getInstance(Application app){
        if (instance == null){
            instance = new TranslationRepo(app);
        }
        return instance;
    }
    private TranslationRepo(Application app){
        AppDatabase appDatabase = AppDatabase.getInstance(app);
        this.mDao = appDatabase.translationsDao();
        this.mSavedT = mDao.getAllTranslations();
    }
    public LiveData<List<Translation>> getFavWords(){
        return this.mSavedT;
    }
    public Single<Translation> findTranslation(String w){
        return this.mDao.findWord(w);
    }
    public void insertTranslation(Translation w){
        new insertAsyncTask(mDao).execute(w);
    }
    public void removeTranslation(Translation w){
        new removeAsyncTask(mDao).execute(w);
    }
    private static class insertAsyncTask extends AsyncTask<Translation, Void, Void> {
        private final TranslationsDao mAsyncTaskDao;
        insertAsyncTask(TranslationsDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(Translation... params) {
            mAsyncTaskDao.insertTranslation(params[0]);
            return null;
        }
    }
    private static class removeAsyncTask extends AsyncTask<Translation, Void, Void> {
        private final TranslationsDao mAsyncTaskDao;
        removeAsyncTask(TranslationsDao dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Translation... params) {
            mAsyncTaskDao.removeTranslation(params[0]);
            return null;
        }
    }
}
