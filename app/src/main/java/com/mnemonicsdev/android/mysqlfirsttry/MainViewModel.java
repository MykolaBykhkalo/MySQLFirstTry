package com.mnemonicsdev.android.mysqlfirsttry;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private Repository repository;
    private MutableLiveData<Boolean> isDataBaseConnected;
    private MutableLiveData<User> receivedUser;

    @SuppressLint("StaticFieldLeak")
    public MainViewModel() {
        receivedUser = new MutableLiveData<>();
        isDataBaseConnected = new MutableLiveData<>();
        repository = Repository.getInstance();

        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected void onPreExecute() {
                isDataBaseConnected.setValue(false);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                return repository.ConnectToDataBase();
            }

            @Override
            protected void onPostExecute(Boolean isConnected) {
                isDataBaseConnected.setValue(isConnected);
            }
        }.execute();

//        if (repository.ConnectToDataBase()) {
//            isDataBaseConnected.setValue(true);
//        } else isDataBaseConnected.setValue(false);
    }

    public MutableLiveData<Boolean> getIsDataBaseConnected() {
        return isDataBaseConnected;
    }

    public void setIsDataBaseConnected(boolean isDataBaseConnected) {
        this.isDataBaseConnected.setValue(isDataBaseConnected);
    }

    public MutableLiveData<User> getReceivedUser() {
        return receivedUser;
    }

    public void setReceivedUser(User user) {
        receivedUser.setValue(user);
    }

    void InsertUserToDataBase(User user) {
        repository.InsertUser(user);
    }

    void GetUserFromDataBaseBySurname(String surname){
        receivedUser.setValue(repository.getUserBySurname(surname));
    }

}



