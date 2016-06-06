package DB;

import java.security.MessageDigest;
import java.sql.*;
import java.util.Formatter;

import iBeaconServer.Member;



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
    public Member getMemberByUserAccountAndPsd(String userAccount, String userPwd) {
        userPwd = encryptPassword(userPwd);
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
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
    
//    public int setOnlineTag(int isOnline, String account) {
//        String sql = "UPDATE member SET OnlineTag = ? WHERE Account = ?";
//        int result = 0;
//        try {
//            PreparedStatement preparedStatement = conn.prepareStatement(sql);
//            preparedStatement.setInt(1, isOnline);
//            preparedStatement.setString(2, account);
//            result = preparedStatement.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return result;
//
//    }
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
    public int setOnlineTag(int isOnline, String account) {
        String sql = "UPDATE user SET OnlineTag = ? WHERE username = ?";
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
}
