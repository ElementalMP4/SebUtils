package main.java.elementalmp4.utils;

public class Profile {

    private String nickname;
    private String colourName;

    public Profile(String nickname, String colourName) {
        this.nickname = nickname;
        this.colourName = colourName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
    }

}
