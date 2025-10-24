package com.example.core_api.navigation;

import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public class FragmentNavigator {
    private final FragmentManager fragmentManager;
    private final int containerId;
    private final static String TAG = "FragmentNavigator";

    public FragmentNavigator(@NonNull FragmentManager fragmentManager, @IdRes int containerId) {
        this.fragmentManager = Objects.requireNonNull(fragmentManager, "fragmentManager");
        this.containerId = containerId;
    }

    public void navigateTo(@NonNull Fragment fragment) {
        navigateTo(fragment, true);
    }

    public void navigateTo(@NonNull Fragment fragment, boolean addToBackStack) {
        Objects.requireNonNull(fragment, "fragment");

        String targetTag = fragment.getClass().getSimpleName();

        Fragment current = fragmentManager.findFragmentById(containerId);
        if (current != null && current.getClass().equals(fragment.getClass())) {
            Log.d(TAG, "Navigation skipped: target fragment is already displayed (" + targetTag + ")");
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, fragment, targetTag);
        if (addToBackStack) {
            transaction.addToBackStack(targetTag);
        }

        transaction.commit();
        Log.d(TAG, "Navigated to " + targetTag + " (addToBackStack=" + addToBackStack + ")");
    }

    public boolean popBackStack() {
        boolean popped = fragmentManager.popBackStackImmediate();
        Log.d(TAG, "popBackStack -> " + popped);
        return popped;
    }

    public void clearBackStack() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            return;
        }

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.d(TAG, "Back stack cleared");
    }

}
