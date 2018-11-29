package com.rba18.helpers;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.rba18.activities.MainActivity;
import com.rba18.model.Apartment;
import com.rba18.model.Lease;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.Tenant;
import com.rba18.model.TypeTotal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Cody on 3/20/2018.
 */

public class MainArrayDataMethods {

    public MainArrayDataMethods() {

    }

    public Tenant getCachedTenantByTenantID(int tenantID) {
        Tenant primaryTenant = null;
        for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
            if (MainActivity.sTenantList.get(i).getId() == tenantID) {
                primaryTenant = MainActivity.sTenantList.get(i);
                break;
            }
        }
        return primaryTenant;
    }

    public Apartment getCachedApartmentByApartmentID(int apartmentID) {
        Apartment apartment = null;
        for (int i = 0; i < MainActivity.sApartmentList.size(); i++) {
            if (MainActivity.sApartmentList.get(i).getId() == apartmentID) {
                apartment = MainActivity.sApartmentList.get(i);
                break;
            }
        }
        return apartment;
    }

    public Tenant getCachedPrimaryTenantByLease(@Nullable Lease lease) {
        if(lease != null) {
            return getCachedTenantByTenantID(lease.getPrimaryTenantID());
        } else {
            return null;
        }
    }

    public Lease getCachedActiveLeaseByApartmentID(int apartmentID) {
        for (int i = 0; i < MainActivity.sCurrentLeasesList.size(); i++) {
            if (MainActivity.sCurrentLeasesList.get(i).getApartmentID() == apartmentID) {
                return MainActivity.sCurrentLeasesList.get(i);

            }
        }
        return null;
    }

    public Lease getCachedActiveLeaseByTenantID(int tenantID) {
        for (int i = 0; i < MainActivity.sCurrentLeasesList.size(); i++) {
            if (MainActivity.sCurrentLeasesList.get(i).getPrimaryTenantID() == tenantID) {
                return MainActivity.sCurrentLeasesList.get(i);
            } else {
                ArrayList<Integer> secondaryTenantIDs = MainActivity.sCurrentLeasesList.get(i).getSecondaryTenantIDs();
                for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                    if (secondaryTenantIDs.get(y) == tenantID) {
                        return MainActivity.sCurrentLeasesList.get(i);
                    }
                }
            }
        }
        return null;
    }

    public Pair<Tenant, ArrayList<Tenant>> getCachedPrimaryAndSecondaryTenantsByLease(@Nullable Lease lease) {
        Tenant primaryTenant = null;
        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        if (lease != null) {
            ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
            for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
                if (MainActivity.sTenantList.get(i).getId() == lease.getPrimaryTenantID()) {
                    primaryTenant = MainActivity.sTenantList.get(i);
                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.sTenantList.get(i).getId()) {
                            secondaryTenants.add(MainActivity.sTenantList.get(i));
                        }
                    }
                }
            }
        }
        return new Pair<>(primaryTenant, secondaryTenants);
    }

    public Pair<Tenant, ArrayList<Tenant>> getCachedSelectedTenantAndRoomMatesByLease(@Nullable Lease lease, int tenantID) {
        Tenant selectedTenant = null;
        ArrayList<Tenant> otherTenants = new ArrayList<>();
        if (lease != null) {
            ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
            for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
                if (MainActivity.sTenantList.get(i).getId() == lease.getPrimaryTenantID()) {
                    if (MainActivity.sTenantList.get(i).getId() == tenantID) {
                        selectedTenant = MainActivity.sTenantList.get(i);
                    } else {
                        otherTenants.add(MainActivity.sTenantList.get(i));
                    }
                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.sTenantList.get(i).getId()) {
                            if (MainActivity.sTenantList.get(i).getId() == tenantID) {
                                selectedTenant = MainActivity.sTenantList.get(i);
                            } else {
                                otherTenants.add(MainActivity.sTenantList.get(i));
                            }
                        }
                    }
                }
            }
        }
        return new Pair<>(selectedTenant, otherTenants);
    }

    public ArrayList<Tenant> getCachedRoomMatesNoPrimaryByLease(@Nullable Lease lease, int tenantID) {
        //Tenant selectedTenant = null;
        ArrayList<Tenant> otherTenants = new ArrayList<>();
        if (lease != null) {
            ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
            for (int i = 0; i < MainActivity.sTenantList.size(); i++) {
                if (MainActivity.sTenantList.get(i).getId() == lease.getPrimaryTenantID()) {

                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.sTenantList.get(i).getId()) {
                            if (MainActivity.sTenantList.get(i).getId() == tenantID) {
                                //selectedTenant = MainActivity.sTenantList.get(i);
                            } else {
                                otherTenants.add(MainActivity.sTenantList.get(i));
                            }
                        }
                    }
                }
            }
        }
        return otherTenants;
    }

    public void sortMainApartmentArray() {
        Collections.sort(MainActivity.sApartmentList, new Comparator<Apartment>() {
            @Override
            public int compare(Apartment apartment, Apartment t1) {
                int b1 = apartment.isRented() ? 1 : 0;
                int b2 = t1.isRented() ? 1 : 0;

                int comp = b2 - b1;
                if (comp != 0) {
                    return comp;
                } else {
                    String s1 = apartment.getStreet1AndStreet2String().replaceAll("[1234567890]", "");
                    String s2 = t1.getStreet1AndStreet2String().replaceAll("[1234567890]", "");
                    //String s1 = apartment.getCity();
                    //String s2 = t1.getCity();
                    return s1.compareTo(s2);
                }
            }
        });
    }

    public void sortMainTenantArray() {
        Collections.sort(MainActivity.sTenantList, new Comparator<Tenant>() {
            @Override
            public int compare(Tenant tenant, Tenant t1) {
                int b1 = (tenant.getHasLease()) ? 1 : 0;
                int b2 = (t1.getHasLease()) ? 1 : 0;

                int comp = b2 - b1;
                if (comp != 0) {
                    return comp;
                } else {
                    String s1 = tenant.getFirstName();
                    String s2 = t1.getFirstName();
                    comp = s1.compareTo(s2);
                    if (comp != 0) {
                        return comp;
                    } else {
                        String os1 = tenant.getLastName();
                        String os2 = tenant.getLastName();
                        return os1.compareTo(os2);
                    }
                }
            }
        });
    }

    public ArrayList<MoneyLogEntry> sortMoneyByDateDesc(ArrayList<MoneyLogEntry> moneyLogEntries){
        Collections.sort(moneyLogEntries, new Comparator<MoneyLogEntry>() {
            @Override
            public int compare(MoneyLogEntry moneyLogEntry, MoneyLogEntry t1) {
                return t1.getDate().compareTo(moneyLogEntry.getDate());
            }
        });
        return moneyLogEntries;
    }

    public ArrayList<MoneyLogEntry> sortMoneyByDateAsc(ArrayList<MoneyLogEntry> moneyLogEntries){
        Collections.sort(moneyLogEntries, new Comparator<MoneyLogEntry>() {
            @Override
            public int compare(MoneyLogEntry moneyLogEntry, MoneyLogEntry t1) {
                return moneyLogEntry.getDate().compareTo(t1.getDate());
            }
        });
        return moneyLogEntries;
    }

    public void sortLeaseArrayByStartDateDesc(ArrayList<Lease> leases){
        Collections.sort(leases, new Comparator<Lease>() {
            @Override
            public int compare(Lease lease, Lease t1) {
                return t1.getLeaseStart().compareTo(lease.getLeaseStart());
            }
        });
    }

    public void sortTenantArrayAlphabetically(ArrayList<Tenant> tenants){
        Collections.sort(tenants, new Comparator<Tenant>() {
            @Override
            public int compare(Tenant tenant, Tenant t1) {
                String s1 = tenant.getFirstName();
                String s2 = t1.getFirstName();
                 int comp = s1.compareTo(s2);
                if (comp != 0) {
                    return comp;
                } else {
                    String os1 = tenant.getLastName();
                    String os2 = t1.getLastName();
                    return os1.compareTo(os2);
                }
            }
        });
    }

    public void sortApartmentArrayAlphabetically(ArrayList<Apartment> apartments){
        Collections.sort(apartments, new Comparator<Apartment>() {
            @Override
            public int compare(Apartment apartment, Apartment t1) {
                String s1 = apartment.getStreet1AndStreet2String().replaceAll("[1234567890]", "");
                String s2 = t1.getStreet1AndStreet2String().replaceAll("[1234567890]", "");
                return s1.compareTo(s2);
            }
        });
    }

    public void sortTypeTotalsArrayByTotalAmountDesc(ArrayList<TypeTotal> typeTotals){
        Collections.sort(typeTotals, new Comparator<TypeTotal>() {
            @Override
            public int compare(TypeTotal typeTotal, TypeTotal t1) {
                return t1.getTotalAmount().compareTo(typeTotal.getTotalAmount());
            }
        });
    }
}
