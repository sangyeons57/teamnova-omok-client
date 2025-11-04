package com.example.feature_home.presentation.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application.dto.response.RankingDataResponse;
import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.RankingDataUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.domain.user.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Loads ranking data from the application layer and exposes it to the dialog UI.
 */
public class RankingDialogViewModel extends ViewModel {

    private static final String TAG = "RankingDialogVM";

    private final RankingDataUseCase rankingDataUseCase;
    private final UserSessionStore userSessionStore;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<RankingRow>> rankingRows = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<RankingRow> selfRow = new MutableLiveData<>();

    public RankingDialogViewModel() {
        UseCaseContainer container = UseCaseContainer.getInstance();
        this.rankingDataUseCase = container.registry.get(RankingDataUseCase.class);
        this.userSessionStore = container.userSessionStore;
        refreshRanking();
    }

    /** Exposes the full ranking list for RecyclerView rendering. */
    @NonNull
    public LiveData<List<RankingRow>> getRankingRows() {
        return rankingRows;
    }

    /** Exposes the current player's ranking summary. */
    @NonNull
    public LiveData<RankingRow> getSelfRow() {
        return selfRow;
    }

    /** Reloads ranking data from the backend. */
    public void refreshRanking() {
        rankingDataUseCase.executeAsync(UseCase.None.INSTANCE, executorService)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.w(TAG, "Ranking load failed", throwable);
                        rankingRows.postValue(Collections.emptyList());
                        selfRow.postValue(null);
                        return;
                    }

                    if (result instanceof UResult.Ok<?> ok) {
                        RankingDataResponse response = (RankingDataResponse) ok.value();
                        List<RankingRow> entries = mapToRows(response.entries());
                        rankingRows.postValue(entries);
                        RankingRow self = resolveSelfRow(response.entries());
                        selfRow.postValue(self);
                    } else if (result instanceof UResult.Err<?> err) {
                        Log.w(TAG, "Ranking load failed: " + err.message());
                        rankingRows.postValue(Collections.emptyList());
                        selfRow.postValue(null);
                    }
                });
    }

    public void onCloseClicked() {
        Log.d(TAG, "Ranking dialog closed");
    }

    private List<RankingRow> mapToRows(@NonNull List<RankingDataResponse.Entry> entries) {
        List<RankingRow> rows = new ArrayList<>(entries.size());
        for (RankingDataResponse.Entry entry : entries) {
            rows.add(new RankingRow(entry.rank, entry.displayName, entry.score));
        }
        return rows;
    }

    @Nullable
    private RankingRow resolveSelfRow(@NonNull List<RankingDataResponse.Entry> entries) {
        User current = userSessionStore.getCurrentUser();
        if (current == null) {
            return null;
        }

        String targetId = current.getUserId().getValue();
        for (RankingDataResponse.Entry entry : entries) {
            if (entry.userId.equals(targetId)) {
                return new RankingRow(entry.rank, entry.displayName, entry.score);
            }
        }

        return new RankingRow(null,
                current.getDisplayName().getValue(),
                current.getScore().getValue());
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }

    /**
     * Presentation model describing a single ranking row.
     */
    public record RankingRow(@Nullable Integer rank,
                             @NonNull String displayName,
                             int score) {

        public RankingRow {
            Objects.requireNonNull(displayName, "displayName");
        }
    }
}
