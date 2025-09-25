package com.example.data.model.http.request;


public enum Path {
    CREATE_ACCOUNT,
    HELLO_WORLD,
    INFO,
    LOGIN,
    LOGOUT,
    DEACTIVATE_ACCOUNT,
    TERMS_ACCEPTANCES,

    CHANGE_NAME,
    CHANGE_PROFILE_ICON,

    SELF_DATA,
    USER_DATA,
    RANKING_DATA,

    ;
    public static final String BASE_PATH = "https://bamsol.net/public/";

    public String toBasePath() {
        return BASE_PATH + this.toString();
    }

    @Override
    public String toString() {
        return switch (this) {
            case HELLO_WORLD -> "hello-world.php";
            case INFO -> "info.php";

            case CREATE_ACCOUNT -> "create-account.php";
            case DEACTIVATE_ACCOUNT -> "deactivate-account.php";
            case TERMS_ACCEPTANCES -> "terms-acceptances.php";
            case LOGIN -> "login.php";
            case LOGOUT -> "logout.php";

            case CHANGE_NAME -> "change-name.php";
            case CHANGE_PROFILE_ICON -> "change-profile-icon.php";

            case SELF_DATA -> "self-data.php";
            case USER_DATA -> "user-data.php";
            case RANKING_DATA -> "ranking-data.php";

        };
    }
}



