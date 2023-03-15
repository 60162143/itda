package com.example.itda.ui.bag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BagViewModel extends ViewModel{
    private MutableLiveData<String> mText;

    public BagViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is bag fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
