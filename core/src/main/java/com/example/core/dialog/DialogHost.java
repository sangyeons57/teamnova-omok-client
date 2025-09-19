package com.example.core.dialog;

import android.os.Bundle;
import android.os.Looper;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Presents dialogs in a LIFO stack so the most recent request is always visible and can be
 * dismissed in reverse order (similar to a back stack).
 */
public final class DialogHost<T extends Enum<T>> {

    private final DialogRegistry<T> registry;
    private final Deque<DialogSession<T>> dialogStack = new ArrayDeque<>();

    private WeakReference<FragmentActivity> activityRef = new WeakReference<>(null);
    private OnBackPressedCallback backPressedCallback;

    public DialogHost(@NonNull DialogRegistry<T> registry) {
        this.registry = registry;
    }

    /**
     * Binds the manager to a host {@link FragmentActivity} so dialogs can be attached to
     * its window and back-press events can be intercepted.
     */
    @MainThread
    public void attach(@NonNull FragmentActivity activity) {
        ensureMainThread();
        FragmentActivity current = activityRef.get();
        if (current == activity && backPressedCallback != null) {
            return;
        }

        if (backPressedCallback != null) {
            backPressedCallback.remove();
        }

        activityRef = new WeakReference<>(activity);
        backPressedCallback = new OnBackPressedCallback(!dialogStack.isEmpty()) {
            @Override
            public void handleOnBackPressed() {
                dismissTop();
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(activity, backPressedCallback);
    }

    /**
     * Detaches the previously attached host. Any visible dialogs are dismissed to prevent
     * window leaks.
     */
    @MainThread
    public void detach(@NonNull FragmentActivity activity) {
        ensureMainThread();
        FragmentActivity current = activityRef.get();
        if (current != activity) {
            return;
        }

        dismissAll();

        if (backPressedCallback != null) {
            backPressedCallback.remove();
            backPressedCallback = null;
        }
        activityRef.clear();
    }

    @MainThread
    public boolean isAttached() {
        ensureMainThread();
        return activityRef.get() != null;
    }

    /**
     * Adds a dialog request with no arguments to the stack and shows it immediately.
     */
    @MainThread
    public void enqueue(@NonNull T type) {
        enqueue(type, null);
    }

    /**
     * Adds a dialog request to the stack and shows it immediately with optional arguments.
     */
    @MainThread
    public void enqueue(@NonNull T type, @Nullable Bundle arguments) {
        ensureMainThread();
        FragmentActivity activity = requireActivity();
        DialogRegistry.DialogEntry<T> entry = registry.getEntry(type);
        DialogRequest<T> request = new DialogRequest<>(type, arguments);

        AlertDialog dialog = entry.getController().create(activity, request);
        dialog.setCancelable(entry.getConfig().isCancelable());
        dialog.setCanceledOnTouchOutside(entry.getConfig().shouldCancelOnTouchOutside());

        DialogSession<T> session = new DialogSession<>(request, dialog);
        dialog.setOnDismissListener(ignored -> handleDismiss(session));

        dialogStack.addLast(session);
        dialog.show();
        updateBackCallbackState();
    }

    /**
     * Dismisses the top-most dialog if one is visible.
     */
    @MainThread
    public void dismissTop() {
        ensureMainThread();
        DialogSession<T> session = dialogStack.peekLast();
        if (session == null) {
            return;
        }
        dismissSession(session, true);
    }

    /**
     * Dismisses the most recently added dialog of the provided type if present in the stack.
     */
    @MainThread
    public void dismiss(@NonNull T type) {
        ensureMainThread();
        Iterator<DialogSession<T>> iterator = dialogStack.descendingIterator();
        while (iterator.hasNext()) {
            DialogSession<T> session = iterator.next();
            if (session.request.getType() == type) {
                dismissSession(session, true);
                break;
            }
        }
    }

    /**
     * Dismisses every dialog currently shown by the manager.
     */
    @MainThread
    public void dismissAll() {
        ensureMainThread();
        while (!dialogStack.isEmpty()) {
            DialogSession<T> session = dialogStack.peekLast();
            if (session == null) {
                break;
            }
            dismissSession(session, true);
        }
    }

    private void handleDismiss(@NonNull DialogSession<T> session) {
        ensureMainThread();
        dismissSession(session, false);
    }

    private void dismissSession(@NonNull DialogSession<T> session, boolean initiatedByManager) {
        if (!session.markRemoved()) {
            return;
        }
        dialogStack.remove(session);

        session.dialog.setOnDismissListener(null);
        if (initiatedByManager && session.dialog.isShowing()) {
            session.dialog.dismiss();
        }

        updateBackCallbackState();
    }

    private void updateBackCallbackState() {
        if (backPressedCallback != null) {
            backPressedCallback.setEnabled(!dialogStack.isEmpty());
        }
    }

    @NonNull
    private FragmentActivity requireActivity() {
        FragmentActivity activity = activityRef.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            throw new IllegalStateException("DialogHost requires an attached FragmentActivity");
        }
        return activity;
    }

    private void ensureMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("DialogHost must be accessed from the main thread");
        }
    }

    private static final class DialogSession<T extends Enum<T>> {
        final DialogRequest<T> request;
        final AlertDialog dialog;
        private boolean removed;

        DialogSession(@NonNull DialogRequest<T> request, @NonNull AlertDialog dialog) {
            this.request = request;
            this.dialog = dialog;
        }

        boolean markRemoved() {
            if (removed) {
                return false;
            }
            removed = true;
            return true;
        }
    }
}
