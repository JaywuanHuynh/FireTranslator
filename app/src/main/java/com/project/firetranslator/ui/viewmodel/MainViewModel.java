package com.project.firetranslator.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.project.firetranslator.data.model.Translation;
import com.project.firetranslator.data.repository.TranslationRepo;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class MainViewModel extends ViewModel {
    private TranslationRepo mRepo;
    private LiveData<List<Translation>> mList;
    public void init(@NonNull Application application){
        mRepo = TranslationRepo.getInstance(application);
        mList = mRepo.getFavWords();
    }
    public LiveData<List<Translation>> getListTranslations(){
        return this.mList;
    }
    public Single<Translation> findTranslation(String w){
        return mRepo.findTranslation(w);
    }
    public void insertTranslation(Translation w){
        mRepo.insertTranslation(w);
    }
    public void removeTranslation(Translation w){
        mRepo.removeTranslation(w);
    }
}
