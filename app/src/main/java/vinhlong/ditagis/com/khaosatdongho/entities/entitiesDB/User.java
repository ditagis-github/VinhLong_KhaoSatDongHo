package vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB;

public class User {
    private String userName;
    private String passWord;
    private String displayName;
    public User() {

    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
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