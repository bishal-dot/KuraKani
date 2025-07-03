package com.example.kurakani.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kurakani.model.MatchModel;

import java.util.ArrayList;
import java.util.List;

public class MatchViewModel extends ViewModel {
    private final MutableLiveData<List<MatchModel>> matchList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<MatchModel>> getMatchList(){
        return matchList;
    }

    public void addMatch(MatchModel match){
        List<MatchModel> currentList = matchList.getValue();
        if (currentList == null) currentList = new ArrayList<>();
        currentList.add(0, match);
        matchList.setValue(currentList);
    }
}
