package com.rawtalent.epass_user.ui.new_application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewApplicationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NewApplicationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}