package Model;

public class Users {
    String username , about;

    public Users(){
    }

    public Users(String username, String about){
            this.username = username;
            this.about= about;
        }

public String getUsername(){
        return username;
}
public void setUsername(String username){
        this.username=username;
}
public String getAbout(){
        return about;
}
public void setAbout(String about){
        this.about=about;
}
}
