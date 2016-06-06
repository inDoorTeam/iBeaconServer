package DB;
import java.sql.*;


public class DBConnect {
    private static Connection con = null;

    
    public DBConnect() { 
        try { 
            
            Class.forName(DBConfig.JDBC_DRIVER); 
            System.out.println("Connecting to database...");
            con = DriverManager.getConnection(DBConfig.DB_URL, DBConfig.USER, DBConfig.PWD);


            System.out.println("Creating statment...");
            con.createStatement();
            
            System.out.println("Connected to database successful...");
        }
        catch(Exception e) { 
            System.out.println("Exception :"+e.toString()); 
        } 
    } 
    public static Connection getConn() {
        if(con == null)
            new DBConnect();
        return con;
    }
    
//    public void SelectTable() { 
//        try { 
//            stat = con.createStatement(); 
//            rs = stat.executeQuery(selectSQL); 
//            System.out.println("NO\t\tusername\t\temail\t\t\t\t\t\tPASSWORD"); 
//            while(rs.next()) {
//                System.out.println(rs.getInt("No") + "\t\t\t"+ 
//                rs.getString("username") + "\t\t" + 
//                rs.getString("email") + "\t\t\t" + rs.getString("password")); 
//            } 
//        } 
//        catch(SQLException e)  { 
//            System.out.println("SelectDB Exception :" + e.toString()); 
//        } 
//    }
//    public boolean SelectTable(String name, String pwd) { 
//        try { 
//            stat = con.createStatement(); 
//            rs = stat.executeQuery(selectSQL); 
//            while(rs.next()) {
//                String queryUser= rs.getString("username");
//                String queryPwd = rs.getString("password");
//                if( queryUser.equals(name) && queryPwd.equals(pwd) ){
//                    System.out.println("Welcome, " + name + "!");
//                    return true;
//                }
//            }
//        }
//        catch(SQLException e)  { 
//            System.out.println("SelectDB Exception :" + e.toString()); 
//        }
//        return false;
//    }
}
