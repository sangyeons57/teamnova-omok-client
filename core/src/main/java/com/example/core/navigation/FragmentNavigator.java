package com.example.core.navigation;

import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public class FragmentNavigator {
    public static class Options {
        public final boolean addToBackStack;
        public final String tag;
        public final int enterAnim;
        public final int exitAnim;
        public final int popEnterAnim;
        public final int popExitAnim;

        private Options(Builder builder) {
            this.addToBackStack = builder.addToBackStack;
            this.tag = builder.tag;
            this.enterAnim = builder.enterAnim;
            this.exitAnim = builder.exitAnim;
            this.popEnterAnim = builder.popEnterAnim;
            this.popExitAnim = builder.popExitAnim;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private boolean addToBackStack = true;
            private String tag;
            private int enterAnim = FragmentTransaction.TRANSIT_NONE;
            private int exitAnim = FragmentTransaction.TRANSIT_NONE;
            private int popEnterAnim = FragmentTransaction.TRANSIT_NONE;
            private int popExitAnim = FragmentTransaction.TRANSIT_NONE;

            public Builder addToBackStack(boolean value) {
                this.addToBackStack = value;
                return this;
            }

            public Builder tag(String value) {
                this.tag = value;
                return this;
            }

            public Builder animations(int enter, int exit, int popEnter, int popExit) {
                this.enterAnim = enter;
                this.exitAnim = exit;
                this.popEnterAnim = popEnter;
                this.popExitAnim = popExit;
                return this;
            }

            public Options build() {
                return new Options(this);
            }
        }
    }

    private final FragmentManager fragmentManager;
    private final int containerId;
    private final String logTag;

    public FragmentNavigator(@NonNull FragmentManager fragmentManager, @IdRes int containerId) {
        this(fragmentManager, containerId, "FragmentNavigator");
    }

    public FragmentNavigator(@NonNull FragmentManager fragmentManager, @IdRes int containerId, @NonNull String logTag) {
        this.fragmentManager = Objects.requireNonNull(fragmentManager, "fragmentManager");
        this.containerId = containerId;
        this.logTag = Objects.requireNonNull(logTag, "logTag");
    }

    public void navigateTo(@NonNull Fragment fragment) {
        navigateTo(fragment, Options.builder().build());
    }

    public void navigateTo(@NonNull Fragment fragment, @NonNull Options options) {
        Objects.requireNonNull(fragment, "fragment");
        Objects.requireNonNull(options, "options");

        String targetTag = options.tag != null ? options.tag : fragment.getClass().getSimpleName();

        Fragment current = fragmentManager.findFragmentById(containerId);
        if (current != null && current.getClass().equals(fragment.getClass())) {
            Log.d(logTag, "Navigation skipped: target fragment is already displayed (" + targetTag + ")");
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (options.enterAnim != FragmentTransaction.TRANSIT_NONE || options.exitAnim != FragmentTransaction.TRANSIT_NONE) {
            transaction.setCustomAnimations(options.enterAnim, options.exitAnim, options.popEnterAnim, options.popExitAnim);
        }

        transaction.replace(containerId, fragment, targetTag);
        if (options.addToBackStack) {
            transaction.addToBackStack(targetTag);
        }

        transaction.commit();
        Log.d(logTag, "Navigated to " + targetTag + " (addToBackStack=" + options.addToBackStack + ")");
    }

    public boolean popBackStack() {
        boolean popped = fragmentManager.popBackStackImmediate();
        Log.d(logTag, "popBackStack -> " + popped);
        return popped;
    }

    public void clearBackStack() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            return;
        }

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.d(logTag, "Back stack cleared");
    }

    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentById(containerId);
    }

    public Fragment findFragmentByTag(@NonNull String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }
}
