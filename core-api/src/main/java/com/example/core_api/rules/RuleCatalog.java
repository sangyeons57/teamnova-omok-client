package com.example.core_api.rules;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central registry holding metadata about optional game rules.
 */
public final class RuleCatalog {

    private static final SparseArray<RuleInfo> RULES;

    static {
        SparseArray<RuleInfo> rules = new SparseArray<>();
        rules.put(1, new RuleInfo(
                1,
                R.string.rule_catalog_rule_convert_stone_title,
                RuleInfo.NO_ICON,
                R.string.rule_catalog_rule_convert_stone_description
        ));
        rules.put(2, new RuleInfo(
                2,
                R.string.rule_catalog_speed_game_title,
                RuleInfo.NO_ICON,
                R.string.rule_catalog_speed_game_description
        ));
        rules.put(3, new RuleInfo(
                3,
                R.string.rule_catalog_disruption_title,
                RuleInfo.NO_ICON,
                R.string.rule_catalog_disruption_description
        ));
        RULES = rules;
    }

    private RuleCatalog() {
        // No instances.
    }

    @NonNull
    public static List<RuleInfo> getAll() {
        List<RuleInfo> items = new ArrayList<>(RULES.size());
        for (int i = 0; i < RULES.size(); i++) {
            items.add(RULES.valueAt(i));
        }
        return Collections.unmodifiableList(items);
    }

    @Nullable
    public static RuleInfo findRule(int id) {
        return RULES.get(id);
    }

    @NonNull
    public static List<RuleInfo> getRules(@NonNull List<Integer> ruleIds) {
        List<RuleInfo> items = new ArrayList<>(ruleIds.size());
        for (int ruleId : ruleIds) {
            RuleInfo info = RULES.get(ruleId);
            if (info != null) {
                items.add(info);
            }
        }
        return Collections.unmodifiableList(items);
    }
}
