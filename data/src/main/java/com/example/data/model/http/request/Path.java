package com.example.data.model.http.request;


public enum Path {
    CREATE_ACCOUNT,
    HELLO_WORLD,
    INFO,
    LOGIN,
    LOGOUT,
    DEACTIVATE_ACCOUNT,
    TERMS_ACCEPTANCES,

    LINK_GOOGLE,

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
        switch (this) {
            case HELLO_WORLD:
                return "hello-world.php";
            case INFO:
                return "info.php";

            case CREATE_ACCOUNT:
                return "create-account.php";
            case DEACTIVATE_ACCOUNT:
                return "deactivate-account.php";
            case TERMS_ACCEPTANCES:
                return "terms-acceptances.php";
            case LOGIN:
                return "login.php";
            case LOGOUT:
                return "logout.php";

            case LINK_GOOGLE:
                return "link-google.php";

            case CHANGE_NAME:
                return "change-name.php";
            case CHANGE_PROFILE_ICON:
                return "change-profile-icon.php";

            case SELF_DATA:
                return "self-data.php";
            case USER_DATA:
                return "user-data.php";
            case RANKING_DATA:
                return "ranking-data.php";

            default:
                throw new IllegalStateException("Unknown path: " + this);
        }
    }
}



