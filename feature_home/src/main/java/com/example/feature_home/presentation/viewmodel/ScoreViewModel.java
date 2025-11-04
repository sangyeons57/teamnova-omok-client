package com.example.feature_home.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.session.UserSessionStore;
import com.example.domain.user.entity.User;
import com.example.domain.user.value.UserScore;
import com.example.feature_home.presentation.model.ScoreMilestone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Supplies score ladder data and current user score information.
 */
public class ScoreViewModel extends ViewModel {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 3000;
    private static final float WINDOW = 100;
    private static final int DEFAULT_SCORE = 0;
    private final MutableLiveData<List<ScoreMilestone>> milestones = new MutableLiveData<>();
    private final MediatorLiveData<Integer> currentScore = new MediatorLiveData<>();
    private final List<ScoreMilestone> milestoneTemplates;

    public ScoreViewModel(@NonNull UserSessionStore userSessionStore) {
        milestoneTemplates = createMilestoneTemplates();
        milestones.setValue(buildMilestones(Collections.emptyMap()));
        currentScore.setValue(DEFAULT_SCORE);
        currentScore.addSource(userSessionStore.getUserStream(), this::updateScoreFromUser);

        User existing = userSessionStore.getCurrentUser();
        if (existing != null) {
            updateScoreFromUser(existing);
        }
    }


    private void updateScoreFromUser(User user) {
        int value = DEFAULT_SCORE;
        if (user != null) {
            UserScore score = user.getScore();
            if (score != null) {
                value = score.getValue();
            }
        }
        if (value < MIN_SCORE) {
            value = MIN_SCORE;
        }
        currentScore.setValue(value);
    }

    @NonNull
    public LiveData<List<ScoreMilestone>> getMilestones() {
        return milestones;
    }

    @NonNull
    public LiveData<Integer> getCurrentScore() {
        return currentScore;
    }

    public int getCurrentScoreValue() {
        Integer value = currentScore.getValue();
        return value != null ? value : DEFAULT_SCORE;
    }

    public float getMinScore() {
        return MIN_SCORE;
    }

    public float getMaxScore() {
        return MAX_SCORE;
    }

    public float getWindowSize() {
        return WINDOW;
    }

    public int getMilestoneCount() {
        return milestoneTemplates.size();
    }

    public int resolveMilestoneIndexForLimit(int limitScore) {
        if (milestoneTemplates.isEmpty()) {
            return 0;
        }
        if (limitScore <= MIN_SCORE) {
            return 0;
        }
        float clamped = Math.min(limitScore, MAX_SCORE);
        float steps = (clamped - MIN_SCORE) / WINDOW;
        int index = (int) Math.ceil(steps);
        int maxIndex = milestoneTemplates.size() - 1;
        if (index < 0) {
            return 0;
        }
        if (index > maxIndex) {
            return maxIndex;
        }
        return index;
    }

    public void assignRulesToMilestones(@NonNull Map<Integer, List<String>> codesByIndex) {
        milestones.setValue(buildMilestones(Objects.requireNonNull(codesByIndex, "codesByIndex == null")));
    }

    private List<ScoreMilestone> createMilestoneTemplates() {
        List<ScoreMilestone> ascending = new ArrayList<>();
        float previousUpper = MIN_SCORE;
        for (int index = 0; ; index++) {
            float upper = MIN_SCORE + (index * WINDOW);
            if (upper > MAX_SCORE) {
                upper = MAX_SCORE;
            }
            float lowerExclusive = (index == 0) ? Float.NEGATIVE_INFINITY : previousUpper;
            ScoreMilestone milestone = new ScoreMilestone(
                    index,
                    upper,
                    lowerExclusive,
                    upper,
                    Collections.emptyList()
            );
            ascending.add(milestone);
            previousUpper = upper;
            if (upper >= MAX_SCORE) {
                break;
            }
        }
        List<ScoreMilestone> descending = new ArrayList<>(ascending.size());
        for (int i = ascending.size() - 1; i >= 0; i--) {
            descending.add(ascending.get(i));
        }
        return Collections.unmodifiableList(descending);
    }

    private List<ScoreMilestone> buildMilestones(@NonNull Map<Integer, List<String>> codesByIndex) {
        List<ScoreMilestone> items = new ArrayList<>(milestoneTemplates.size());
        for (ScoreMilestone template : milestoneTemplates) {
            List<String> codes = codesByIndex.get(template.getWindowIndex());
            if (codes == null) {
                codes = Collections.emptyList();
            }
            items.add(template.withRuleCodes(codes));
        }
        return Collections.unmodifiableList(items);
    }
}
