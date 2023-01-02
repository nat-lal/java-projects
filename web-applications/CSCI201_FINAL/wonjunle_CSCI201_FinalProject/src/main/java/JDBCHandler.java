
import java.sql.*; // to save space
import java.util.ArrayList;
public class JDBCHandler {
	
	final static String SQL_USER = "root";
	final static String SQL_PASSWORD = "rootpass";
	final static String SQL_CONNECTION = "jdbc:mysql://localhost:3306/?user=" + SQL_USER + "&password=" + SQL_PASSWORD;
	
	public static int register (String username, String password){
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn = null;
        Statement st = null;
        int userID = -1;
        String saved_password = "";

        try {
            conn = DriverManager.getConnection(SQL_CONNECTION);
            st = conn.createStatement();
//            st.execute("INSERT INTO mydb.credentials (user_id, password) VALUES ('" + username + "', '" + password + "');");
            ResultSet rs1 = st.executeQuery("SELECT user_id, password FROM mydb.credentials where username LIKE ('" + username + "')");
            if (rs1.next()) {
                userID = rs1.getInt("user_id");
                saved_password = rs1.getString("password");
            }
            System.out.println("userExists = "+ userID);
            if (userID == -1) {
            	st.execute("INSERT INTO mydb.credentials (username, password) VALUES ('" + username + "', '" + password + "');");
            }
        } catch (SQLException sqle) {
            System.out.println("SQLException in registerUser");
            sqle.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                System.out.println("sql e: " + sqle.getMessage());
            }
        }

        return userID;
	}
	
	public static int loginR (String username, String password){
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn = null;
        Statement st = null;
        int userID = -1;
        String saved_password = "";

        try {
            conn = DriverManager.getConnection(SQL_CONNECTION);
            st = conn.createStatement();
            ResultSet rs1 = st.executeQuery("SELECT user_id, password FROM mydb.credentials where username LIKE ('" + username + "')");
            if (rs1.next()) {
                userID = rs1.getInt("user_id");
                saved_password = rs1.getString("password");
            }
            System.out.println("userExists = "+ userID);
            if (userID != -1 ) {
                if (!password.equals(saved_password)) {
                    //if password mismatch, then we set userID = -2
                    userID = -2;
                }
            }
        } catch (SQLException sqle) {
            System.out.println("SQLException in registerUser");
            sqle.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                System.out.println("sql e: " + sqle.getMessage());
            }
        }

        return userID;
	}
	
	public static ArrayList<Course> searchResultQuery (String statement) {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} 
		catch (ClassNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} 
		
		ArrayList<Course> courses = new ArrayList<>();
    	
    	Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(SQL_CONNECTION);
			st = conn.createStatement();
			rs = st.executeQuery(statement);
			while (rs.next()) {
				String courseID = rs.getString("course_id_string");
				String courseName = rs.getString("course_name_long");
				int startTime = rs.getInt("start_time");
				int endTime = rs.getInt("end_time");
				String professor = rs.getString("name");
				double rating = rs.getDouble("rating");
				String day = "";
				if (rs.getInt("monday") == 1) {
					day += "M";
				}
				if (rs.getInt("tuesday") == 1) {
					day += "T";
				}
				if (rs.getInt("wednesday") == 1) {
					day += "W";
				}
				if (rs.getInt("thursday") == 1) {
					day += "Th";
				}
				if (rs.getInt("friday") == 1) {
					day += "F";
				}
				
				
				if(startTime != 0 && endTime != 0) {
					courses.add(new Course(courseID, courseName, startTime, endTime, professor, rating, day));
				}
			}
			
			rs.close();
		 } catch (SQLException sqle) {
			 System.out.println (sqle.getMessage());
		 } finally {
			 try {
				 if (rs != null) {
					 rs.close();
				 }
				 if (st != null) {
					 st.close();
				 }
				 if (conn != null) {
					 conn.close();
				 }
			 } catch (SQLException sqle) {
				 System.out.println(sqle.getMessage());
			 }
		 }
		
		 return courses;
		 
	}
}
