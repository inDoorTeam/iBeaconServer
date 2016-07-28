package DB;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;

import iBeaconServer.Item;
import iBeaconServer.Member;
import iBeaconServer.User;


public class GuideDB {
    private Connection con = null;
    private static GuideDB guideDB = null;

    private GuideDB(){
        con = DBConnect.getConn();
    }
    public static GuideDB getInstance() {
        if(guideDB == null)
            guideDB = (new GuideDB());
        return guideDB;
    }

    public ArrayList<Item> getItemListByuser(User user){

        String sql = "SELECT * FROM ItemList WHERE username = ?";

        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserAccount());
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Item> itemList = new ArrayList<>();
            while (resultSet.next()) {
                Item item = new Item();
                item.setOwner(resultSet.getString("username"));
                item.setItemName(resultSet.getString("item"));
                item.setMinor(resultSet.getInt("Minor"));
                item.setLocation(resultSet.getString("location"));
                itemList.add(item);

            }

            return itemList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Item getItem(int minor){
        String sql = "SELECT * FROM ItemList WHERE minor = ?";

        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(minor));
            ResultSet resultSet = preparedStatement.executeQuery();
            Item item = new Item();
            while (resultSet.next()) {
                item.setOwner(resultSet.getString("username"));
                item.setItemName(resultSet.getString("item"));
            }
            return item;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Member getMemberByUserAccountAndPsd(String userAccount, String userPwd) {
        userPwd = encryptPassword(userPwd);
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
        Member member = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, userAccount);
            preparedStatement.setString(2, userPwd);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if(member == null)
                    member = new Member();
                member.setAccount(resultSet.getString("username"));
                member.setPwd(resultSet.getString("password"));;
            }

            return member;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String byteToHex(byte[] digest) {
      // TODO Auto-generated method stub
      Formatter formatter = new Formatter();
      for (byte b : digest){
          formatter.format("%02x", b);
      }
      String result = formatter.toString();
      formatter.close();
      return result;
    }

    private static String encryptPassword(String password){
        String sha1 = "";
        try{
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return sha1;
    }

    public void SelectTable() {
        try {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(DBConfig.selectSQL);
            System.out.println("NO\t\tusername\t\temail\t\t\t\t\t\tPASSWORD");
            while(rs.next()) {
                System.out.println(rs.getInt("No") + "\t\t\t"+
                rs.getString("username") + "\t\t" +
                rs.getString("email") + "\t\t\t" + rs.getString("password"));
            }
        }
        catch(SQLException e)  {
            System.out.println("SelectDB Exception :" + e.toString());
        }
    }
    public ArrayList<Member> getFriendListByUser(User user){
        String sql = "SELECT * from User WHERE username = binary ?";
        Member friend = null;
        ArrayList<Member> friendList = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserAccount());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if(friend == null)
                    friend = new Member();
                friend.setAccount(resultSet.getString("username"));
                friendList.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendList;

    }
    public boolean isFriend(User user, User otherUser){
        String sql = "SELECT * from FriendShip WHERE user = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserAccount());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if(otherUser.getUserAccount().equalsIgnoreCase(resultSet.getString("userfriend"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int setOnlineTag(int isOnline, String account) {
        String sql = "UPDATE User SET OnlineTag = ? WHERE username = binary ?";
        int result = 0;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, isOnline);
            preparedStatement.setString(2, account);
            result = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return result;

    }
    public int setItemLocation(Item item, String location) {
        String sql = "UPDATE ItemList SET location = ? WHERE username = ?";
        int result = 0;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, location);
            preparedStatement.setString(2, item.getOwner());
            result = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void selectFromItemList() {
        try {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(DBConfig.selectSQL);
            System.out.println("ID\t\tusername\t\tlocation\t\t\t\t\t\titem");
            while(rs.next()) {
                System.out.println(rs.getInt("no") + "\t\t\t"+
                        rs.getString("username") + "\t\t" +
                        rs.getString("location") + "\t\t\t" + rs.getString("item"));
            }
        }
        catch(SQLException e)  {
            System.out.println("SelectDB Exception :" + e.toString());
        }

    }
    public boolean isMyItem(User user, int minor){
        String sql = "SELECT * FROM ItemList WHERE username = ? AND minor = ?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, user.getUserAccount());
            preparedStatement.setString(2, minor+"");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println(user.getUserAccount() + " has " + minor);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(user.getUserAccount() + " doesnt have " + minor);
        return false;
    }
}
