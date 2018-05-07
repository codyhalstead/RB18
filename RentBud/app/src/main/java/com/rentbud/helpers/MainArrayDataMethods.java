package com.rentbud.helpers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.rentbud.activities.MainActivity;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.Lease;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.Tenant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/20/2018.
 */

public class MainArrayDataMethods {

    public MainArrayDataMethods() {
    }

    public Tenant getCachedTenantByTenantID(int tenantID) {
        Tenant primaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == tenantID) {
                primaryTenant = MainActivity.tenantList.get(i);
                break;
            }
        }
        return primaryTenant;
    }

    public Apartment getCachedApartmentByApartmentID(int apartmentID) {
        Apartment apartment = null;
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).getId() == apartmentID) {
                apartment = MainActivity.apartmentList.get(i);
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
        for (int i = 0; i < MainActivity.currentLeasesList.size(); i++) {
            if (MainActivity.currentLeasesList.get(i).getApartmentID() == apartmentID) {
                return MainActivity.currentLeasesList.get(i);

            }
        }
        return null;
    }

    public Lease getCachedActiveLeaseByTenantID(int tenantID) {
        for (int i = 0; i < MainActivity.currentLeasesList.size(); i++) {
            if (MainActivity.currentLeasesList.get(i).getPrimaryTenantID() == tenantID) {
                return MainActivity.currentLeasesList.get(i);
            } else {
                ArrayList<Integer> secondaryTenantIDs = MainActivity.currentLeasesList.get(i).getSecondaryTenantIDs();
                for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                    if (secondaryTenantIDs.get(y) == tenantID) {
                        return MainActivity.currentLeasesList.get(i);
                    }
                }
            }
        }
        return null;
    }

    //public ArrayList<Tenant> getCachedSecondaryTenantsByApartmentID(int apartmentID) {
    //    ArrayList<Tenant> secondaryTenants = new ArrayList<>();
    //    for (int i = 0; i < MainActivity5.tenantList.size(); i++) {
    //        if (MainActivity5.tenantList.get(i).getApartmentID() == apartmentID && !MainActivity5.tenantList.get(i).getIsPrimary()) {
    //            secondaryTenants.add(MainActivity5.tenantList.get(i));
    //        }
    //    }
    //    return secondaryTenants;
    //}

    public Pair<Tenant, ArrayList<Tenant>> getCachedPrimaryAndSecondaryTenantsByLease(@Nullable Lease lease) {
        Tenant primaryTenant = null;
        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        if (lease != null) {
            ArrayList<Integer> secondaryTenantIDs = lease.getSecondaryTenantIDs();
            for (int i = 0; i < MainActivity.tenantList.size(); i++) {
                if (MainActivity.tenantList.get(i).getId() == lease.getPrimaryTenantID()) {
                    primaryTenant = MainActivity.tenantList.get(i);
                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.tenantList.get(i).getId()) {
                            secondaryTenants.add(MainActivity.tenantList.get(i));
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
            for (int i = 0; i < MainActivity.tenantList.size(); i++) {
                if (MainActivity.tenantList.get(i).getId() == lease.getPrimaryTenantID()) {
                    if (MainActivity.tenantList.get(i).getId() == tenantID) {
                        selectedTenant = MainActivity.tenantList.get(i);
                    } else {
                        otherTenants.add(MainActivity.tenantList.get(i));
                    }
                } else {
                    for (int y = 0; y < secondaryTenantIDs.size(); y++) {
                        if (secondaryTenantIDs.get(y) == MainActivity.tenantList.get(i).getId()) {
                            if (MainActivity.tenantList.get(i).getId() == tenantID) {
                                selectedTenant = MainActivity.tenantList.get(i);
                            } else {
                                otherTenants.add(MainActivity.tenantList.get(i));
                            }
                        }
                    }
                }
            }
        }
        return new Pair<>(selectedTenant, otherTenants);
    }

    public void sortMainApartmentArray() {
        Collections.sort(MainActivity.apartmentList, new Comparator<Apartment>() {
            @Override
            public int compare(Apartment apartment, Apartment t1) {
                int b1 = apartment.isRented() ? 1 : 0;
                int b2 = t1.isRented() ? 1 : 0;

                int comp = b2 - b1;
                if (comp != 0) {
                    return comp;
                } else {
                    String s1 = apartment.getCity();
                    String s2 = t1.getCity();
                    return s1.compareTo(s2);
                }
            }
        });
    }

    public void sortMainTenantArray() {
        Collections.sort(MainActivity.tenantList, new Comparator<Tenant>() {
            @Override
            public int compare(Tenant tenant, Tenant t1) {
                int b1 = (tenant.getHasLease()) ? 1 : 0;
                int b2 = (t1.getHasLease()) ? 1 : 0;

                int comp = b2 - b1;
                if (comp != 0) {
                    return comp;
                } else {
                    String s1 = tenant.getFirstName();
                    String s2 = tenant.getFirstName();
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

    // public ExpenseLogEntry getCachedExpenseByID(int expenseID) {
    //     ExpenseLogEntry expense = null;
    //     for (int i = 0; i < MainActivity5.expenseList.size(); i++) {
    //         if (MainActivity5.expenseList.get(i).getId() == expenseID) {
    //             expense = MainActivity5.expenseList.get(i);
    //             break;
    //         }
    //     }
    //     return expense;
    // }

    // public PaymentLogEntry getCachedIncomeByID(int incomeID) {
    //     PaymentLogEntry income = null;
    //     for (int i = 0; i < MainActivity5.incomeList.size(); i++) {
    //         if (MainActivity5.incomeList.get(i).getId() == incomeID) {
    //             income = MainActivity5.incomeList.get(i);
    //             break;
    //         }
    //     }
    //     return income;
    // }

    // public void sortMainIncomeArray() {
    //     Collections.sort(MainActivity5.incomeList, new Comparator<PaymentLogEntry>() {
    //         @Override
    //         public int compare(PaymentLogEntry ple, PaymentLogEntry p1) {
    //
    //             return ple.getPaymentDate().compareTo(p1.getPaymentDate());
    //         }
    //     });
    // }

    // public void sortMainExpenseArray() {
    //     Collections.sort(MainActivity5.expenseList, new Comparator<ExpenseLogEntry>() {
    //         @Override
    //         public int compare(ExpenseLogEntry ele, ExpenseLogEntry e1) {

    //             return ele.getExpenseDate().compareTo(e1.getExpenseDate());
    //         }
    //     });
    // }
}
