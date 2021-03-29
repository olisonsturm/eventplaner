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
    private String eMail;
    private String password;

    public boolean checkLoginData(String userName, String password) {
        // MySQL-Abfrage hier hin
        if (userName.equals("olison") && password.equals("sturm")) {
            this.userName = userName;
            this.password = password;
            return true;
        } else {
            return false;
        }
    }

    public int checkRegisterData(String userName, String eMail, String password, String passwordAgain) {
        // MySQL-Abfrage hier hin
        if (eMail.contains("@") && password.equals(passwordAgain) && passwordAgain.equals(password)) {
            this.userName = userName;
            this.eMail = eMail;
            this.password = password;
            return 0;

        } else {
            /*
             * 1 = registerErrorUserName
             * 2 = registerErrorUserName
             * */
            if (userName.equals("olison")) {
                return 1;
            } else if (!(password.equals(passwordAgain))) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    public String getUserName() {
        return userName;
    }
}
