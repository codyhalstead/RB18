package com.rba18.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.activities.NewLeaseWizard;
import com.rba18.adapters.LeaseListAdapter;
import com.rba18.helpers.ApartmentTenantViewModel;
import com.rba18.model.Lease;
import com.rba18.sqlite.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class DateViewFrag2 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    public DateViewFrag2() {
        // Required empty public constructor
    }

    private TextView mNoLeasesTV;
    private LeaseListAdapter mLeaseListAdapter;
    private ColorStateList mAccentColor;
    private ListView mListView;
    private DatabaseHandler mDB;
    private Lease mSelectedLease;
    private OnLeaseDataChangedListener mCallback;
    private PopupMenu mPopupMenu;
    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoLeasesTV = view.findViewById(R.id.emptyListTV);
        FloatingActionButton fab = view.findViewById(R.id.listFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewLeaseWizard.class);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                intent.putExtra("preloadedStartDate", formatter.format(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getDate().getValue()));
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
            }
        });
        LinearLayout totalBarLL = view.findViewById(R.id.moneyListTotalBarLL);
        totalBarLL.setVisibility(View.GONE);
        mListView = view.findViewById(R.id.mainMoneyListView);
        mDB = new DatabaseHandler(getContext());
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        mAccentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setUpSearchBar() {

    }

    public interface OnLeaseDataChangedListener{
        void onLeaseDataChanged();
        void onLeasePaymentsChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnLeaseDataChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLeaseDataChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void setUpListAdapter() {
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getMoneyArray().getValue() != null) {
            mLeaseListAdapter = new LeaseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue(),
                    mAccentColor, ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getDate().getValue());
            mListView.setAdapter(mLeaseListAdapter);
            mListView.setOnItemClickListener(this);
            mNoLeasesTV.setText(R.string.no_leases_to_display_date);
            mListView.setEmptyView(mNoLeasesTV);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mPopupMenu = new PopupMenu(getActivity(), view);
        MenuInflater inflater = mPopupMenu.getMenuInflater();
        final int position = i;
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.edit:
                        //On mListView row click, launch ApartmentViewActivity passing the rows data into it.
                        Intent intent = new Intent(getActivity(), NewLeaseWizard.class);
                        mSelectedLease = mLeaseListAdapter.getFilteredResults().get(position);
                        intent.putExtra("leaseToEdit", mSelectedLease);
                        startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                        return true;

                    case R.id.remove:
                        mSelectedLease = mLeaseListAdapter.getFilteredResults().get(position);
                        showDeleteConfirmationAlertDialog();
                        return true;

                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.lease_click_menu, mPopupMenu.getMenu());
        mPopupMenu.show();
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.lease_deletion_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDB.setLeaseInactive(mSelectedLease);
                showDeleteAllRelatedMoneyAlertDialog();
            }
        });
        // add the buttons
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // create and show the alert mDialog
        mDialog = builder.create();
        mDialog.show();
    }

    public void showDeleteAllRelatedMoneyAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.lease_related_money_deletion_confirmation);
        // add the buttons
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCallback.onLeaseDataChanged();
            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDB.setAllExpensesRelatedToLeaseInactive(mSelectedLease.getId());
                mDB.setAllIncomeRelatedToLeaseInactive(mSelectedLease.getId());
                mCallback.onLeaseDataChanged();
                mCallback.onLeasePaymentsChanged();
            }
        });
        // create and show the alert mDialog
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mPopupMenu != null){
            mPopupMenu.dismiss();
        }
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                mCallback.onLeaseDataChanged();
                mCallback.onLeasePaymentsChanged();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void updateData(){
        mLeaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue());
        mLeaseListAdapter.notifyDataSetChanged();
    }
}