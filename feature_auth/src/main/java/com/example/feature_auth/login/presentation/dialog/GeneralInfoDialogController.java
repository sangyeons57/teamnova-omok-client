package com.example.feature_auth.login.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.core_api.dialog.DialogArgumentKeys;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.GeneralInfoContentType;
import com.example.core_api.dialog.MainDialogType;
import com.example.feature_auth.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

/**
 * Presents static information such as the privacy policy or terms of service.
 */
public final class GeneralInfoDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_general_info, null, false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();

        MaterialTextView titleView = contentView.findViewById(R.id.textGeneralInfoTitle);
        MaterialTextView bodyView = contentView.findViewById(R.id.textGeneralInfoBody);
        MaterialButton closeButton = contentView.findViewById(R.id.buttonGeneralInfoClose);

        GeneralInfoContentType contentType = resolveContentType(request);
        bindContent(contentType, titleView, bodyView);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindContent(@NonNull GeneralInfoContentType type,
                             @NonNull MaterialTextView titleView,
                             @NonNull MaterialTextView bodyView) {
        switch (type) {
            case PRIVACY_POLICY:
                titleView.setText(R.string.general_info_privacy_title);
                bodyView.setText(R.string.general_info_privacy_body);
                break;
            case TERMS_OF_SERVICE:
            default:
                titleView.setText(R.string.general_info_terms_title);
                bodyView.setText(R.string.general_info_terms_body);
                break;
        }
    }

    @NonNull
    private GeneralInfoContentType resolveContentType(@NonNull DialogRequest<MainDialogType> request) {
        String rawType = request.getArguments().getString(DialogArgumentKeys.GENERAL_INFO_TYPE);
        if (rawType == null || rawType.trim().isEmpty()) {
            return GeneralInfoContentType.TERMS_OF_SERVICE;
        }
        try {
            return GeneralInfoContentType.valueOf(rawType);
        } catch (IllegalArgumentException ex) {
            return GeneralInfoContentType.TERMS_OF_SERVICE;
        }
    }
}
