package com.example.application.port.out.user;

import com.example.application.dto.command.ChangeNameCommand;
import com.example.application.dto.command.ChangeProfileIconCommand;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserProfileIcon;

public interface UserRepository {
    boolean changeName(UserDisplayName newName);
    boolean changeProfileIcon(UserProfileIcon newIcon);
}
