package com.project.firetranslator.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.project.firetranslator.R;
import com.project.firetranslator.data.model.Translation;
import com.project.firetranslator.databinding.ActivityMainBinding;
import com.project.firetranslator.databinding.ActivitySavedBinding;
import com.project.firetranslator.ui.adapter.MyAdapter;
import com.project.firetranslator.ui.viewmodel.MainViewModel;

import java.util.List;

public class SavedActivity extends AppCompatActivity {
    private ActivitySavedBinding binding;
    private MainViewModel mViewModel;
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.init(getApplication());
        mAdapter = new MyAdapter(getApplicationContext());
        RecyclerView.LayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());
        binding.rvSaved.setLayoutManager(linearLayout);
        binding.rvSaved.setAdapter(mAdapter);
        mViewModel.getListTranslations().observe(this, new Observer<List<Translation>>() {
            @Override
            public void onChanged(List<Translation> translations) {
                mAdapter.setSavedList(translations);
            }
        });
    }
}