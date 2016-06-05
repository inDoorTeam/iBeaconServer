package iBeaconServer;

public class Member {
    private String id;
    private String pwd;
    private String gender;
    private String birthday;
    private boolean isAdmin;
    private String store_id;

    public Member() {

    }


    public String getAccount() {
        return id;
    }

    public void setAccount(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isAdminByChinese() {
        return isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getStore_Id() {
        return store_id;
    }

    public void setStore_Id(String store_id) {
        this.store_id = store_id;
    }

    @Override
    public String toString() {
        return "Member [Id: " + id +
                ", pwd: " + pwd +
                ", gender: " + gender +
                ", birthday: " + birthday +
                ", isAdmin: " + isAdmin +
                ", store_id: " + store_id +
                "]";
    }
}
