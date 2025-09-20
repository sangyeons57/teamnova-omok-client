package com.example.data.model.http.request;


public enum Path {
    CREATE_ACCOUNT,
    HELLO_WORLD,
    INFO,
    LOGIN,
    TERMS_ACCEPTANCES
    ;

    @Override
    public String toString() {
        return switch (this) {
            case HELLO_WORLD -> "hello-world.php";
            case INFO -> "info.php";

            case CREATE_ACCOUNT -> "create-account.php";
            case TERMS_ACCEPTANCES -> "terms-acceptances.php";
            case LOGIN -> "login.php";

        };
    }
}



