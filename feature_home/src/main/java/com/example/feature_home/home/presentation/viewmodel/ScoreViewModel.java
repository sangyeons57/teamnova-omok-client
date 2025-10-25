package com.example.feature_home.home.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.session.UserSessionStore;
import com.example.domain.user.entity.User;
import com.example.domain.user.value.UserScore;
import com.example.feature_home.home.presentation.model.RuleCode;
import com.example.feature_home.home.presentation.model.ScoreMilestone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Supplies score ladder data and current user score information.
 */
public class ScoreViewModel extends ViewModel {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 3000;
    private static final float WINDOW = 100;
    private static final int DEFAULT_SCORE = 0;
    private final MutableLiveData<List<ScoreMilestone>> milestones = new MutableLiveData<>(createMilestones());
    private final MediatorLiveData<Integer> currentScore = new MediatorLiveData<>();


    public ScoreViewModel(@NonNull UserSessionStore userSessionStore) {
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

    private List<ScoreMilestone> createMilestones() {
        List<ScoreMilestone> items = new ArrayList<>();
        RuleCode[] codes = RuleCode.values();
        int codeCount = codes.length;
        int index = 0;
        for (float score = MAX_SCORE; score >= MIN_SCORE; score -= WINDOW, index++) {
            RuleCode code = codes[index % codeCount];
            List<String> assignedCode = Collections.singletonList(code.getValue());
            items.add(new ScoreMilestone(score, assignedCode));
        }
        return Collections.unmodifiableList(items);
    }
}
