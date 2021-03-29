package de.morgroup.eventplaner;

public class Model {

    private static Model instance;

    private Model() {
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }


    private String userName;
    private String password;

    public boolean checkLoginData(String userName, String password) {
        if (userName.equals("oli") && password.equals("oli")) {
            this.userName = userName;
            this.password = password;
            return true;
        } else {
            return false;
        }
    }

    public String getUserName() {
        return userName;
    }
}
