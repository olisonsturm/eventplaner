package de.morgroup.eventplaner.model;

public class User implements Comparable<User>{

    private String uid;
    private String photourl;
    private String firstname;
    private String lastname;
    private String nickname;
    private String email;
    private String mobile;

    @Override
    public int compareTo(User o) {
        return getFirstname().compareTo(o.getFirstname());
    }

    public User() {}

    public User(String uid, String firstname, String lastname, String nickname, String email, String mobile) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.nickname = nickname;
        this.email = email;
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
