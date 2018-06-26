package com.rentbud.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.rentbud.adapters.CustomCalendarAdapter;
import com.rentbud.sqlite.DatabaseHandler;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Date;
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
    //CaldroidGridAdapter caldroidGridAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //leaseStartDatesHM = (HashMap<Integer, Integer>) getActivity().getIntent().getSerializableExtra("s");
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        //leaseStartDatesHM.put(3, 5);
        //leaseStartDatesHM.put(7, 7);
        //leaseStartDatesHM.put(5, 5);
        //leaseEndDatesHM.put(28, 1);
        //leaseEndDatesHM.put(7, 5);
        //expenseDatesHM.put(13, 3);
        //expenseDatesHM.put(7, 1);
        //incomeDatesHM.put(22, 7);
        //incomeDatesHM.put(7, 9);
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