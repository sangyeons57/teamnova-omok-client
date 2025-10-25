package com.example.feature_home.home.presentation.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.usecase.LoadRulesCatalogUseCase;
import com.example.application.usecase.RuleIconSource;
import com.example.application.usecase.RuleIconSourceMapper;
import com.example.designsystem.rule.RuleExplainDialog;
import com.example.core_api.navigation.AppNavigationKey;
import com.example.core_api.navigation.FragmentNavigationHost;
import com.example.core_api.navigation.FragmentNavigationHostOwner;
import com.example.core_di.sound.SoundEffects;
import com.example.core_di.UseCaseContainer;
import com.example.feature_home.R;
import com.example.feature_home.home.di.ScoreViewModelFactory;
import com.example.feature_home.home.presentation.adapter.ScoreMilestoneAdapter;
import com.example.feature_home.home.presentation.model.RuleCode;
import com.example.feature_home.home.presentation.ui.widget.WindowedGuageView;
import com.example.feature_home.home.presentation.viewmodel.ScoreViewModel;
import com.example.domain.rules.Rule;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreFragment extends Fragment {

    private FragmentNavigationHost<AppNavigationKey> fragmentNavigationHost;
    private ScoreViewModel viewModel;
    private WindowedGuageView gaugeView;
    private ScoreMilestoneAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView scoreList;
    private TextView scoreTitle;
    private int currentScoreValue = 0;
    private ExecutorService rulesExecutor;
    private Handler mainHandler;
    private LoadRulesCatalogUseCase loadRulesCatalogUseCase;
    private final Map<String, Rule> ruleCatalog = new LinkedHashMap<>();
    private final Map<String, RuleIconSource> iconSources = new LinkedHashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UseCaseContainer container = UseCaseContainer.getInstance();
        loadRulesCatalogUseCase = container.loadRulesCatalogUseCase;
        rulesExecutor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigationHostOwner<?>) {
            @SuppressWarnings("unchecked")
            FragmentNavigationHostOwner<AppNavigationKey> owner = (FragmentNavigationHostOwner<AppNavigationKey>) context;
            fragmentNavigationHost = owner.getFragmentNavigatorHost();
        } else {
            throw new IllegalStateException("Host must provide FragmentNavigatorHost");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ScoreViewModelFactory factory = ScoreViewModelFactory.create();
        viewModel = new ViewModelProvider(this, factory).get(ScoreViewModel.class);

        gaugeView = view.findViewById(R.id.viewScoreGauge);
        scoreList = view.findViewById(R.id.recyclerScoreMilestones);
        Button backButton = view.findViewById(R.id.buttonScoreBack);
        TextView banner = view.findViewById(R.id.textScoreBanner);
        scoreTitle = view.findViewById(R.id.textScoreTitle);

        gaugeView.setReversed(false);

        currentScoreValue = viewModel.getCurrentScoreValue();
        scoreTitle.setText(getString(R.string.score_screen_current_score_format, currentScoreValue));
        gaugeView.configure(viewModel.getMinScore(), viewModel.getMaxScore(), viewModel.getWindowSize(), viewModel.getMaxScore() - currentScoreValue);

        adapter = new ScoreMilestoneAdapter(this::onRuleIconClicked);
        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        scoreList.setLayoutManager(layoutManager);
        scoreList.setAdapter(adapter);
        scoreList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int offset = recyclerView.computeVerticalScrollOffset();
                float scrollScore = gaugeView.pixelToScore(offset);
                float worldSize = gaugeView.getWorldSize();
                float windowStartScore = gaugeView.isReversed()
                        ? worldSize - scrollScore
                        : scrollScore;
                windowStartScore = Math.max(0f, Math.min(worldSize, windowStartScore));
                gaugeView.setWindowStart(windowStartScore);
            }
        });

        viewModel.getMilestones().observe(getViewLifecycleOwner(), milestones -> {
            adapter.submitList(milestones);
            scoreList.post(() -> {
                layoutManager.scrollToPosition(Math.max(adapter.getItemCount() - 1, 0));
                int totalScrollRange = scoreList.computeVerticalScrollRange();
                if (totalScrollRange > 0) {
                    gaugeView.setContentMetrics(totalScrollRange, viewModel.getMaxScore() - viewModel.getMinScore());
                    int currentOffset = scoreList.computeVerticalScrollOffset();
                    float scrollScore = gaugeView.pixelToScore(currentOffset);
                    float worldSize = gaugeView.getWorldSize();
                    float windowStartScore = gaugeView.isReversed()
                            ? worldSize - scrollScore
                            : scrollScore;
                    windowStartScore = Math.max(0f, Math.min(worldSize, windowStartScore));
                    gaugeView.setWindowStart(windowStartScore);
                }
            });
        });

        viewModel.getCurrentScore().observe(getViewLifecycleOwner(), score -> {
            currentScoreValue = score != null ? score : 0;
            scoreTitle.setText(getString(R.string.score_screen_current_score_format, currentScoreValue));
            gaugeView.configure(viewModel.getMinScore(), viewModel.getMaxScore(), viewModel.getWindowSize(), viewModel.getMaxScore() -  currentScoreValue);
        });


        setClickWithSound(backButton, this::navigateHome);
        setClickWithSound(banner, this::navigateHome);

        preloadRuleMetadata();
    }

    @Override
    public void onDetach() {
        fragmentNavigationHost = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (rulesExecutor != null) {
            rulesExecutor.shutdownNow();
            rulesExecutor = null;
        }
        super.onDestroy();
    }

    private void navigateHome() {
        if (fragmentNavigationHost == null) {
            return;
        }
        boolean popped = fragmentNavigationHost.popBackStack();
        if (!popped) {
            fragmentNavigationHost.navigateTo(AppNavigationKey.HOME, false);
        }
    }

    private void onRuleIconClicked(@NonNull String ruleCode) {
        SoundEffects.playButtonClick();
        RuleExplainDialog.present(getParentFragmentManager(), ruleCode);
    }

    private void setClickWithSound(@NonNull View view, @NonNull Runnable action) {
        view.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            action.run();
        });
    }

    private void preloadRuleMetadata() {
        if (rulesExecutor == null) {
            return;
        }
        rulesExecutor.execute(() -> {
            LinkedHashMap<String, Rule> orderedCatalog = new LinkedHashMap<>();
            LinkedHashMap<String, RuleIconSource> orderedIcons = new LinkedHashMap<>();
            boolean loaded = false;
            try {
                List<Rule> allRules = loadRulesCatalogUseCase.execute();
                Map<String, Rule> rulesByCode = new HashMap<>(allRules.size());
                Map<String, RuleIconSource> iconsByCode = new HashMap<>(allRules.size());
                for (Rule rule : allRules) {
                    String codeValue = rule.getCode().getValue();
                    rulesByCode.put(codeValue, rule);
                    iconsByCode.put(codeValue, RuleIconSourceMapper.fromPath(rule.getIconPath()));
                }
                List<String> orderedCodes = RuleCode.orderedValues();
                for (String codeValue : orderedCodes) {
                    Rule matchedRule = rulesByCode.get(codeValue);
                    orderedCatalog.put(codeValue, matchedRule);
                    RuleIconSource resolved = iconsByCode.getOrDefault(codeValue, RuleIconSource.none());
                    orderedIcons.put(codeValue, resolved);
                }
                loaded = true;
            } catch (Exception ignored) {
                // Keep existing maps when loading fails.
            }
            final boolean loadSucceeded = loaded;
            mainHandler.post(() -> {
                if (!isAdded() || adapter == null || !loadSucceeded) {
                    return;
                }
                ruleCatalog.clear();
                ruleCatalog.putAll(orderedCatalog);
                iconSources.clear();
                iconSources.putAll(orderedIcons);
                adapter.updateRuleCatalog(ruleCatalog);
                adapter.updateIconSources(iconSources);
            });
        });
    }
}
