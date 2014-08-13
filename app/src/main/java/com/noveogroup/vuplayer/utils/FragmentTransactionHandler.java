/*******************************************************************************
 * Copyright (c) 2014 Sergey Bragin and Alexandr Valov
 ******************************************************************************/

package com.noveogroup.vuplayer.utils;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentTransactionHandler {

    private FragmentTransactionHandler() {
        throw new UnsupportedOperationException("FragmentTransactionHandler" +
                " instance can not be created.");
    }

    public static void putFragment(FragmentManager fragmentManager, int containerId,
                                   Fragment fragment, String tag, boolean doAdd) {
        fragmentManager.executePendingTransactions();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, tag);
        if(doAdd) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

    }
}
