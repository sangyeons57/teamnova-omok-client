package com.example.designsystem.rule;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.application.port.in.UResult;
import com.example.application.usecase.FindRuleByCodeUseCase;
import com.example.application.usecase.RuleIconSource;
import com.example.application.usecase.ResolveRuleIconSourceUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.designsystem.R;
import com.example.domain.rules.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Factory helpers for rendering rule icons consistently across modules.
 */
public final class RuleIconRenderer {

    private static final ExecutorService ICON_EXECUTOR = Executors.newFixedThreadPool(2);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private static volatile FindRuleByCodeUseCase findRuleByCodeUseCase;
    private static volatile ResolveRuleIconSourceUseCase resolveRuleIconSourceUseCase;
    private static final Object USE_CASE_LOCK = new Object();

    private RuleIconRenderer() {
    }

    @NonNull
    public static View createIconView(@NonNull Context context,
                                      @NonNull String ruleCode,
                                      @Nullable ViewGroup parent) {
        View iconView = inflateIconView(context, parent);
        bindIconView(iconView, ruleCode, null, null);
        return iconView;
    }

    @NonNull
    public static View createIconView(@NonNull Context context,
                                      @NonNull String ruleCode,
                                      @Nullable Rule rule,
                                      @Nullable RuleIconSource iconSource,
                                      @Nullable ViewGroup parent) {
        return createIconView(context, ruleCode, rule, iconSource, parent, true);
    }

    @NonNull
    public static View createIconView(@NonNull Context context,
                                      @NonNull String ruleCode,
                                      @Nullable Rule rule,
                                      @Nullable RuleIconSource iconSource,
                                      @Nullable ViewGroup parent,
                                      boolean allowAsyncFetch) {
        View iconView = inflateIconView(context, parent);
        bindIconView(iconView, ruleCode, rule, iconSource, allowAsyncFetch);
        return iconView;
    }

    public static void bindIconView(@NonNull View iconView,
                                    @NonNull String ruleCode,
                                    @Nullable Rule rule,
                                    @Nullable RuleIconSource iconSource) {
        bindIconView(iconView, ruleCode, rule, iconSource, true);
    }

    public static void bindIconView(@NonNull View iconView,
                                    @NonNull String ruleCode,
                                    @Nullable Rule rule,
                                    @Nullable RuleIconSource iconSource,
                                    boolean allowAsyncFetch) {
        Objects.requireNonNull(iconView, "iconView == null");
        ImageView imageView = resolveImageView(iconView);
        iconView.setTag(R.id.designsystem_tag_rule_code, ruleCode);
        iconView.setTag(R.id.designsystem_tag_rule_info, rule);

        CharSequence placeholderLabel = iconView.getContext()
                .getString(R.string.designsystem_rule_dialog_unknown_title);
        iconView.setContentDescription(placeholderLabel);
        imageView.setContentDescription(placeholderLabel);
        imageView.setImageDrawable(null);

        if (rule != null) {
            applyRuleText(iconView, rule);
        }
        if (iconSource != null) {
            applyIconSource(iconView, iconSource);
        }

        boolean needsDetails = rule == null;
        boolean needsIcon = iconSource == null;
        if (allowAsyncFetch && (needsDetails || needsIcon)) {
            loadRuleInfoAsync(iconView, ruleCode, needsDetails, needsIcon);
        }
    }

    private static View inflateIconView(@NonNull Context context, @Nullable ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_rule_icon_button, parent, false);
    }

    private static void applyRuleText(@NonNull View iconView, @NonNull Rule rule) {
        CharSequence label = rule.getName();
        iconView.setTag(R.id.designsystem_tag_rule_info, rule);
        iconView.setContentDescription(label);
        ImageView imageView = resolveImageView(iconView);
        imageView.setContentDescription(label);
    }

    private static void applyIconSource(@NonNull View iconView, @NonNull RuleIconSource iconSource) {
        ImageView imageView = resolveImageView(iconView);
        if (!iconSource.isPresent()) {
            imageView.setImageDrawable(null);
            return;
        }
        String ruleCode = (String) iconView.getTag(R.id.designsystem_tag_rule_code);
        if (iconSource.getType() == RuleIconSource.Type.DRAWABLE_RESOURCE) {
            Drawable drawable = loadDrawableFromResources(iconView.getContext(), iconSource.getValue());
            imageView.setImageDrawable(drawable);
            return;
        }
        if (iconSource.getType() == RuleIconSource.Type.ASSET) {
            ICON_EXECUTOR.execute(() -> {
                Drawable drawable = loadDrawableFromAssets(iconView.getContext().getApplicationContext(), iconSource.getValue());
                MAIN_HANDLER.post(() -> {
                    if (!ruleCode.equals(iconView.getTag(R.id.designsystem_tag_rule_code))) {
                        return;
                    }
                    imageView.setImageDrawable(drawable);
                });
            });
        }
    }

    private static void loadRuleInfoAsync(@NonNull View iconView,
                                          @NonNull String ruleCode,
                                          boolean fetchDetails,
                                          boolean fetchIcon) {
        ICON_EXECUTOR.execute(() -> {
            Rule rule = null;
            if (fetchDetails) {
                UResult<Rule> detailResult = obtainFindUseCase().execute(ruleCode);
                if (detailResult instanceof UResult.Ok) {
                    @SuppressWarnings("unchecked")
                    UResult.Ok<Rule> ok = (UResult.Ok<Rule>) detailResult;
                    rule = ok.value();
                }
            }
            RuleIconSource iconSource = null;
            if (fetchIcon) {
                UResult<RuleIconSource> iconResult = obtainResolveUseCase().execute(ruleCode);
                if (iconResult instanceof UResult.Ok) {
                    @SuppressWarnings("unchecked")
                    UResult.Ok<RuleIconSource> ok = (UResult.Ok<RuleIconSource>) iconResult;
                    iconSource = ok.value();
                }
            }
            Rule finalRule = rule;
            RuleIconSource finalIconSource = iconSource;
            MAIN_HANDLER.post(() -> {
                if (!ruleCode.equals(iconView.getTag(R.id.designsystem_tag_rule_code))) {
                    return;
                }
                if (finalRule != null) {
                    applyRuleText(iconView, finalRule);
                }
                if (finalIconSource != null) {
                    applyIconSource(iconView, finalIconSource);
                }
            });
        });
    }

    @NonNull
    private static ImageView resolveImageView(@NonNull View iconView) {
        ImageView imageView = iconView.findViewById(R.id.imageRuleIcon);
        if (imageView == null && iconView instanceof ImageView) {
            imageView = (ImageView) iconView;
        }
        if (imageView == null) {
            throw new IllegalArgumentException("iconView must contain imageRuleIcon ImageView");
        }
        return imageView;
    }

    @Nullable
    private static Drawable loadDrawableFromResources(@NonNull Context context, @Nullable String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            return null;
        }
        int resId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resId == 0) {
            return null;
        }
        return AppCompatResources.getDrawable(context, resId);
    }

    @Nullable
    private static Drawable loadDrawableFromAssets(@NonNull Context context, @Nullable String assetPath) {
        if (assetPath == null || assetPath.trim().isEmpty()) {
            return null;
        }
        AssetManager assetManager = context.getAssets();
        try (InputStream stream = assetManager.open(assetPath)) {
            Drawable drawable = Drawable.createFromStream(stream, assetPath);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            return drawable;
        } catch (IOException ignored) {
            return null;
        }
    }

    @NonNull
    private static FindRuleByCodeUseCase obtainFindUseCase() {
        FindRuleByCodeUseCase current = findRuleByCodeUseCase;
        if (current == null) {
            synchronized (USE_CASE_LOCK) {
                current = findRuleByCodeUseCase;
                if (current == null) {
                    current = UseCaseContainer.getInstance().get(FindRuleByCodeUseCase.class);
                    findRuleByCodeUseCase = current;
                }
            }
        }
        return current;
    }

    @NonNull
    private static ResolveRuleIconSourceUseCase obtainResolveUseCase() {
        ResolveRuleIconSourceUseCase current = resolveRuleIconSourceUseCase;
        if (current == null) {
            synchronized (USE_CASE_LOCK) {
                current = resolveRuleIconSourceUseCase;
                if (current == null) {
                    current = UseCaseContainer.getInstance().get(ResolveRuleIconSourceUseCase.class);
                    resolveRuleIconSourceUseCase = current;
                }
            }
        }
        return current;
    }
}
