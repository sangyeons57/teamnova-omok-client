package com.example.application.port.out.user;

import com.example.domain.user.entity.RankingEntry;
import com.example.domain.user.entity.User;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserId;
import com.example.domain.user.value.UserProfileIcon;

import java.util.List;

public interface UserRepository {
    /**
     * @return null when the request succeeded, otherwise the error message produced by the backend.
     */
    String changeName(UserDisplayName newName);
    boolean changeProfileIcon(UserProfileIcon newIcon);
    List<RankingEntry> fetchRankingData();
    User fetchSelfData();
    User fetchUserData(UserId userId);
}
