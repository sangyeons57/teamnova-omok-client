package com.example.feature_game.game.presentation.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.application.port.in.UResult;
import com.example.application.session.GameInfoStore;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameTurnState;
import com.example.application.session.OmokBoardState;
import com.example.application.session.OmokBoardStore;
import com.example.application.usecase.FindRuleByCodeUseCase;
import com.example.application.usecase.ResolveRuleIconSourceUseCase;
import com.example.application.usecase.RuleIconSource;
import com.example.core_di.UseCaseContainer;
import com.example.domain.rules.Rule;
import com.example.feature_game.game.presentation.state.GameInfoDialogEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Supplies overlay state for the game info dialog.
 */
public class GameInfoDialogViewModel extends ViewModel {

    private static final String TAG = "GameInfoDialogVM";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore omokBoardStore;
    private final FindRuleByCodeUseCase findRuleByCodeUseCase;
    private final ResolveRuleIconSourceUseCase resolveRuleIconSourceUseCase;
    private final ExecutorService ruleExecutor;
    private final Handler mainHandler;
    private final Map<String, Rule> ruleCache = new LinkedHashMap<>();
    private final Map<String, RuleIconSource> iconCache = new LinkedHashMap<>();
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    private final MutableLiveData<GameInfoDialogEvent> events = new MutableLiveData<>();
    private final MutableLiveData<GameTurnState> turnState = new MutableLiveData<>(GameTurnState.idle());
    private final MutableLiveData<OmokBoardState> boardState = new MutableLiveData<>(OmokBoardState.empty());
    private final MutableLiveData<GameParticipantInfo> activeParticipant = new MutableLiveData<>();
    private final MutableLiveData<List<String>> activeRuleCodes = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<List<RuleIconState>> activeRuleIcons = new MutableLiveData<>(Collections.emptyList());

    private final Observer<GameTurnState> turnObserver = this::onTurnUpdated;
    private final Observer<OmokBoardState> boardObserver = this::onBoardUpdated;
    private final Observer<List<String>> rulesObserver = this::onRulesUpdated;

    public GameInfoDialogViewModel(@NonNull GameInfoStore gameInfoStore,
                                   @NonNull OmokBoardStore omokBoardStore) {
        this.gameInfoStore = gameInfoStore;
        this.omokBoardStore = omokBoardStore;
        UseCaseContainer container = UseCaseContainer.getInstance();
        this.findRuleByCodeUseCase = container.get(FindRuleByCodeUseCase.class);
        this.resolveRuleIconSourceUseCase = container.get(ResolveRuleIconSourceUseCase.class);
        this.ruleExecutor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());

        GameTurnState initialTurn = gameInfoStore.getTurnStateStream().getValue();
        if (initialTurn == null) {
            initialTurn = gameInfoStore.getCurrentTurnState();
        }
        if (initialTurn != null) {
            turnState.setValue(initialTurn);
        }

        OmokBoardState initialBoard = omokBoardStore.getBoardStateStream().getValue();
        if (initialBoard == null) {
            initialBoard = omokBoardStore.getCurrentBoardState();
        }
        if (initialBoard != null) {
            boardState.setValue(initialBoard);
        }

        GameParticipantInfo participant = gameInfoStore.getCurrentTurnParticipant();
        if (participant != null) {
            activeParticipant.setValue(participant);
        }

        List<String> initialRules = gameInfoStore.getActiveRulesStream().getValue();
        if (initialRules == null || initialRules.isEmpty()) {
            initialRules = gameInfoStore.getCurrentRules();
        }
        if (initialRules != null) {
            activeRuleCodes.setValue(initialRules);
            refreshRuleIcons(initialRules);
        }

        gameInfoStore.getTurnStateStream().observeForever(turnObserver);
        omokBoardStore.getBoardStateStream().observeForever(boardObserver);
        gameInfoStore.getActiveRulesStream().observeForever(rulesObserver);
    }

    @NonNull
    public LiveData<List<String>> getActiveRuleCodes() {
        return activeRuleCodes;
    }

    @NonNull
    public LiveData<List<RuleIconState>> getActiveRuleIcons() {
        return activeRuleIcons;
    }

    @NonNull
    public LiveData<GameTurnState> getTurnState() {
        return turnState;
    }

    @NonNull
    public LiveData<OmokBoardState> getBoardState() {
        return boardState;
    }

    @NonNull
    public LiveData<GameParticipantInfo> getActiveParticipant() {
        return activeParticipant;
    }

    @NonNull
    public LiveData<GameInfoDialogEvent> getEvents() {
        return events;
    }

    public void onCloseClicked() {
        events.setValue(GameInfoDialogEvent.DISMISS);
    }

    public void requestDismiss() {
        events.postValue(GameInfoDialogEvent.DISMISS);
    }

    public void onEventHandled() {
        events.setValue(null);
    }

    private void onTurnUpdated(@Nullable GameTurnState state) {
        if (state == null) {
            state = GameTurnState.idle();
        }
        turnState.postValue(state);
        GameParticipantInfo participant = gameInfoStore.getCurrentTurnParticipant();
        activeParticipant.postValue(participant);
    }

    private void onBoardUpdated(@Nullable OmokBoardState state) {
        if (state == null) {
            state = OmokBoardState.empty();
        }
        boardState.postValue(state);
    }

    private void onRulesUpdated(@Nullable List<String> rules) {
        if (rules == null) {
            rules = Collections.emptyList();
        }
        List<String> snapshot = Collections.unmodifiableList(new ArrayList<>(rules));
        activeRuleCodes.postValue(snapshot);
        refreshRuleIcons(snapshot);
    }

    @Override
    protected void onCleared() {
        gameInfoStore.getTurnStateStream().removeObserver(turnObserver);
        omokBoardStore.getBoardStateStream().removeObserver(boardObserver);
        gameInfoStore.getActiveRulesStream().removeObserver(rulesObserver);
        isDestroyed.set(true);
        ruleExecutor.shutdownNow();
        super.onCleared();
    }

    private void refreshRuleIcons(@NonNull List<String> ruleCodes) {
        if (ruleCodes.isEmpty()) {
            activeRuleIcons.postValue(Collections.emptyList());
            return;
        }
        ruleExecutor.execute(() -> {
            List<RuleIconState> states = new ArrayList<>(ruleCodes.size());
            for (String raw : ruleCodes) {
                if (raw == null) {
                    continue;
                }
                String code = raw.trim();
                if (code.isEmpty()) {
                    continue;
                }
                Rule rule = resolveRule(code);
                RuleIconSource iconSource = resolveIcon(code);
                states.add(new RuleIconState(code, rule, iconSource));
            }
            if (!isDestroyed.get()) {
                mainHandler.post(() -> activeRuleIcons.setValue(states));
            }
        });
    }

    @Nullable
    private Rule resolveRule(@NonNull String code) {
        Rule cached = ruleCache.get(code);
        if (cached != null) {
            return cached;
        }
        UResult<Rule> result = findRuleByCodeUseCase.execute(code);
        if (result instanceof UResult.Ok<?>) {
            @SuppressWarnings("unchecked")
            UResult.Ok<Rule> ok = (UResult.Ok<Rule>) result;
            Rule value = ok.value();
            if (value != null) {
                ruleCache.put(code, value);
            }
            return value;
        }
        if (result instanceof UResult.Err<?>) {
            Log.w(TAG, "Rule lookup failed code=" + code + " reason=" + result);
        }
        return null;
    }

    @Nullable
    private RuleIconSource resolveIcon(@NonNull String code) {
        RuleIconSource cached = iconCache.get(code);
        if (cached != null) {
            return cached;
        }
        UResult<RuleIconSource> result = resolveRuleIconSourceUseCase.execute(code);
        if (result instanceof UResult.Ok<?>) {
            @SuppressWarnings("unchecked")
            UResult.Ok<RuleIconSource> ok = (UResult.Ok<RuleIconSource>) result;
            RuleIconSource value = ok.value();
            if (value != null) {
                iconCache.put(code, value);
            }
            return value;
        }
        if (result instanceof UResult.Err<?>) {
            Log.w(TAG, "Icon lookup failed code=" + code + " reason=" + result);
        }
        return null;
    }

    public static final class RuleIconState {
        @NonNull
        private final String code;
        @Nullable
        private final Rule rule;
        @Nullable
        private final RuleIconSource iconSource;

        private RuleIconState(@NonNull String code,
                              @Nullable Rule rule,
                              @Nullable RuleIconSource iconSource) {
            this.code = Objects.requireNonNull(code, "code == null");
            this.rule = rule;
            this.iconSource = iconSource;
        }

        @NonNull
        public String getCode() {
            return code;
        }

        @Nullable
        public Rule getRule() {
            return rule;
        }

        @Nullable
        public RuleIconSource getIconSource() {
            return iconSource;
        }
    }
}
