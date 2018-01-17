package com.rentbud.fragments;

import com.rentbud.adapters.CustomCalendarAdapter;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

/**
 * Created by Cody on 1/10/2018.
 */

public class CustomCaldroidFragment extends CaldroidFragment {

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CustomCalendarAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }
}
