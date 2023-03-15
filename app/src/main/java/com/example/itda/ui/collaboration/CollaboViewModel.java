package com.example.itda.ui.collaboration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CollaboViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CollaboViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is collabo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}