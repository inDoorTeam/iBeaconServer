package DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConfig {
    public static final String DB_URL = "jdbc:mysql://localhost/guidingdb?";
    public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String USER = "guiding";
    public static final String PWD = "guidinguser";
    public static final ResultSet rs = null; 
    public static final PreparedStatement pst = null; 
    public static final String selectSQL = "select * from ItemList";
}


