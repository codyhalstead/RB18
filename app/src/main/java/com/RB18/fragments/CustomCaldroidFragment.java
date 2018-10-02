package com.RB18.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.RB18.adapters.CustomCalendarAdapter;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.HashMap;

/**
 * Created by Cody on 1/10/2018.
 */

public class CustomCaldroidFragment extends CaldroidFragment {
    //Class may be used to further customize calendar
    private HashMap<String, Integer> leaseStartDatesHM = new HashMap<>();
    private HashMap<String, Integer> leaseEndDatesHM = new HashMap<>();
    private HashMap<String, Integer> expenseDatesHM = new HashMap<>();
    private HashMap<String, Integer> incomeDatesHM = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CustomCalendarAdapter(getActivity(), month, year,
                getCaldroidData(), extraData, leaseStartDatesHM, leaseEndDatesHM, expenseDatesHM, incomeDatesHM);
    }

    public void setEventIcons(HashMap<String, Integer> leaseStartDatesAndAmounts,
                              HashMap<String, Integer> leaseEndDatesAndAmounts,
                              HashMap<String, Integer> expenseDatesAndAmounts,
                              HashMap<String, Integer> incomeDatesAndAmounts) {
        this.leaseStartDatesHM = leaseStartDatesAndAmounts;
        this.leaseEndDatesHM = leaseEndDatesAndAmounts;
        this.expenseDatesHM = expenseDatesAndAmounts;
        this.incomeDatesHM = incomeDatesAndAmounts;
    }

}