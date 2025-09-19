package com.example.data.common.model.request;


public enum Path {
    CREATE_ACCOUNT,
    DELETE_ACCOUNT,
    HELLO_WORLD,
    INFO,
    LOGIN,
    LOGOUT,
    REFRESH_TOKEN,
    TERMS_ACCEPTANCES
    ;

    @Override
    public String toString() {
        return switch (this) {
            case HELLO_WORLD -> "hello-world.php";
            case INFO -> "info.php";

            case CREATE_ACCOUNT -> "create-account.php";
            case DELETE_ACCOUNT -> "delete-account.php";
            case TERMS_ACCEPTANCES -> "terms-acceptances.php";
            case LOGIN -> "login.php";
            case LOGOUT -> "logout.php";

            case REFRESH_TOKEN -> "refresh-token.php";

        };
    }
}



