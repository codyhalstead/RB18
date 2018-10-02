package com.RB18.fragments;

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

import com.example.cody.rentbud.R;
import com.RB18.activities.MainActivity;
import com.RB18.activities.NewLeaseWizard;
import com.RB18.adapters.LeaseListAdapter;
import com.RB18.helpers.ApartmentTenantViewModel;
import com.RB18.model.Apartment;
import com.RB18.model.Lease;
import com.RB18.sqlite.DatabaseHandler;

import static android.app.Activity.RESULT_OK;

public class ApartmentViewFrag3 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public ApartmentViewFrag3() {
        // Required empty public constructor
    }

    TextView noLeaseTV;
    FloatingActionButton fab;
    LinearLayout totalBarLL;
    LeaseListAdapter leaseListAdapter;
    ColorStateList accentColor;
    ListView listView;
    private DatabaseHandler db;
    private Lease selectedLease;
    private Apartment apartment;
    private OnLeaseDataChangedListener mCallback;
    private AlertDialog dialog;
    private PopupMenu popupMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noLeaseTV = view.findViewById(R.id.emptyListTV);
        this.totalBarLL = view.findViewById(R.id.moneyListTotalBarLL);
        totalBarLL.setVisibility(View.GONE);
        this.fab = view.findViewById(R.id.listFab);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.db = new DatabaseHandler(getContext());
        this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewLeaseWizard.class);
                intent.putExtra("preloadedApartment", apartment);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
            }
        });
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        //setTotalTV();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (popupMenu != null) {
            popupMenu.dismiss();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void setUpSearchBar() {

    }

    public interface OnLeaseDataChangedListener {
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
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue() != null) {
            leaseListAdapter = new LeaseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue(), accentColor, null);
            listView.setAdapter(leaseListAdapter);
            listView.setOnItemClickListener(this);
            noLeaseTV.setText(R.string.no_leases_to_display_apartment);
            listView.setEmptyView(noLeaseTV);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        popupMenu = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        final int position = i;
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.edit:
                        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                        Intent intent = new Intent(getActivity(), NewLeaseWizard.class);
                        selectedLease = leaseListAdapter.getFilteredResults().get(position);
                        intent.putExtra("leaseToEdit", selectedLease);
                        startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                        //intent.putExtra("expenseID", expense.getId());
                        //startActivity(intent);
                        return true;

                    case R.id.remove:
                        selectedLease = leaseListAdapter.getFilteredResults().get(position);
                        showDeleteConfirmationAlertDialog();
                        return true;

                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.lease_click_menu, popupMenu.getMenu());
        popupMenu.show();
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("AlertDialog");
        builder.setMessage(R.string.lease_deletion_confirmation);

        // add the buttons
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.setLeaseInactive(selectedLease);
                showDeleteAllRelatedMoneyAlertDialog();
            }
        });

        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    public void showDeleteAllRelatedMoneyAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("AlertDialog");
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
                db.setAllExpensesRelatedToLeaseInactive(selectedLease.getId());
                db.setAllIncomeRelatedToLeaseInactive(selectedLease.getId());
                mCallback.onLeaseDataChanged();
                mCallback.onLeasePaymentsChanged();
            }
        });

        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //currentFilteredLeases = db.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
                //leaseListAdapter.updateResults(currentFilteredLeases);
                //leaseListAdapter.notifyDataSetChanged();
                mCallback.onLeaseDataChanged();
                mCallback.onLeasePaymentsChanged();
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                //total = getTotal();
                //setTotalTV();
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

    public void updateData() {
        // currentFilteredLeases = db.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
        leaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue());
        leaseListAdapter.notifyDataSetChanged();
        //this.total = getTotal();
        //setTotalTV();
    }
}