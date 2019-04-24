package vinhlong.ditagis.com.capnhatdongho.entities.entitiesDB;

public class User {
    private String userName;
    private String passWord;
    private String displayName;
    public User() {

    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}