package com.example.core_di.tcp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.session.OmokStoneType;

import java.util.Locale;

public final class StoneTypeMapper {

    private StoneTypeMapper() {
    }

    @NonNull
    public static OmokStoneType fromNetworkLabel(@Nullable String stoneLabel) {
        if (stoneLabel == null) {
            return OmokStoneType.UNKNOWN;
        }
        String trimmed = stoneLabel.trim();
        if (trimmed.isEmpty()) {
            return OmokStoneType.UNKNOWN;
        }

        OmokStoneType fromIndex = parseIndexedStone(trimmed);
        if (fromIndex != OmokStoneType.UNKNOWN) {
            return fromIndex;
        }

        String normalized = trimmed.toUpperCase(Locale.US);
        if ("0".equals(normalized) || "PLAYER1".equals(normalized) || "RED".equals(normalized)) {
            return OmokStoneType.RED;
        }
        if ("1".equals(normalized) || "PLAYER2".equals(normalized) || "BLUE".equals(normalized)) {
            return OmokStoneType.BLUE;
        }
        if ("2".equals(normalized) || "PLAYER3".equals(normalized) || "YELLOW".equals(normalized)) {
            return OmokStoneType.YELLOW;
        }
        if ("3".equals(normalized) || "PLAYER4".equals(normalized) || "GREEN".equals(normalized)) {
            return OmokStoneType.GREEN;
        }
        if ("4".equals(normalized) || "JOKER".equals(normalized) || "WHITE".equals(normalized)) {
            return OmokStoneType.JOKER;
        }
        if ("5".equals(normalized) || "BLOCKER".equals(normalized) || "BLACK".equals(normalized)) {
            return OmokStoneType.BLOCKER;
        }
        try {
            return OmokStoneType.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return OmokStoneType.UNKNOWN;
        }
    }

    @NonNull
    public static OmokStoneType fromCellValue(int cellValue) {
        if (cellValue == -1) {
            return OmokStoneType.EMPTY;
        }
        int normalized = cellValue & 0xFF;
        if (normalized == 0xFF) {
            return OmokStoneType.EMPTY;
        }
        switch (normalized) {
            case 0:
                return OmokStoneType.RED;
            case 1:
                return OmokStoneType.BLUE;
            case 2:
                return OmokStoneType.YELLOW;
            case 3:
                return OmokStoneType.GREEN;
            case 4:
                return OmokStoneType.JOKER;
            case 5:
                return OmokStoneType.BLOCKER;
            default:
                return OmokStoneType.UNKNOWN;
        }
    }

    @NonNull
    private static OmokStoneType parseIndexedStone(@NonNull String stoneLabel) {
        try {
            int index = Integer.parseInt(stoneLabel);
            switch (index) {
                case 0:
                    return OmokStoneType.RED;
                case 1:
                    return OmokStoneType.BLUE;
                case 2:
                    return OmokStoneType.YELLOW;
                case 3:
                    return OmokStoneType.GREEN;
                case 4:
                    return OmokStoneType.JOKER;
                case 5:
                    return OmokStoneType.BLOCKER;
                default:
                    return OmokStoneType.UNKNOWN;
            }
        } catch (NumberFormatException ignored) {
            return OmokStoneType.UNKNOWN;
        }
    }
}
