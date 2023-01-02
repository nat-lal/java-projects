

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.util.*;



/**
 * Servlet implementation class ResultServlet
 */
@WebServlet("/ResultServlet")
public class ResultServlet extends HttpServlet {
	protected class TimeRange {
		int startTime;
		int endTime;
		
		protected TimeRange(int startTime, int endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
	}
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResultServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    // read the request and collect time ranges that is unavailable
    protected ArrayList<TimeRange> handleTime(HttpServletRequest request, String day){
    	LinkedList<Integer> unavailableTime = new LinkedList<>();
    	
    	if (request.getParameter(day+"89am") == null) {
    		unavailableTime.add(8);
    	}
    	if (request.getParameter(day+"910am") == null) {
    		unavailableTime.add(9);
    	}
    	if (request.getParameter(day+"1011am") == null) {
    		unavailableTime.add(10);
    	}
    	if (request.getParameter(day+"1112pm") == null) {
    		unavailableTime.add(11);
    	}
    	if (request.getParameter(day+"121pm") == null) {
    		unavailableTime.add(12);
    	}
    	if (request.getParameter(day+"12pm") == null) {
    		unavailableTime.add(13);
    	}
    	if (request.getParameter(day+"23pm") == null) {
    		unavailableTime.add(14);
    	}
    	if (request.getParameter(day+"34pm") == null) {
    		unavailableTime.add(15);
    	}
    	if (request.getParameter(day+"45pm") == null) {
    		unavailableTime.add(16);
    	}
    	if (request.getParameter(day+"56pm") == null) {
    		unavailableTime.add(17);
    	}
    	if (request.getParameter(day+"67pm") == null) {
    		unavailableTime.add(18);
    	}
    	if (request.getParameter(day+"78pm") == null) {
    		unavailableTime.add(19);
    	}
    	if (request.getParameter(day+"89pm") == null) {
    		unavailableTime.add(20);
    	}
    	if (request.getParameter(day+"910pm") == null) {
    		unavailableTime.add(21);
    	}
    	
    	if (unavailableTime.size() == 0) {
    		return new ArrayList<TimeRange>();
    	}
    	
    	
    	ArrayList<TimeRange> times = new ArrayList<>();
    	int hour = unavailableTime.removeFirst();
    	times.add(new TimeRange(hour, hour+1));
    	while (unavailableTime.size() != 0) {
    		hour = unavailableTime.removeFirst();
    		if (times.get(times.size()-1).endTime == hour) {
    			times.get(times.size()-1).endTime += 1;
    		}
    		else {
    			times.add(new TimeRange(hour, hour+1));
    		}
    	}
    	
    	
    	return times;
    }
    
    // build statement using the time and day
    protected String buildStatement(ArrayList<TimeRange> time, String day, String statement) {
    	for (int i = 0; i < time.size(); i++) {
    		TimeRange range = time.get(i);
    		statement += " OR (courses." + day + "=1 AND ((courses.start_time BETWEEN " 
    				+ range.startTime + " AND " + range.endTime + ") OR (courses.end_time BETWEEN "
    				+ range.startTime + " AND " + range.endTime + ") OR (courses.start_time <= "
    				+ range.startTime + " AND courses.end_time >= " + range.endTime +")))";
    	}
    	return statement;
    }
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String ge = request.getParameter("ge-type");
    	
    	
    	// read request
    	ArrayList<TimeRange> mon = handleTime(request, "M");
    	ArrayList<TimeRange> tue = handleTime(request, "T");
    	ArrayList<TimeRange> wed = handleTime(request, "W");
    	ArrayList<TimeRange> thu = handleTime(request, "TH");
    	ArrayList<TimeRange> fri = handleTime(request, "F");
    	
    	for (int i = 0; i < mon.size(); i++) {
    		System.out.println(mon.get(i).startTime + " " + mon.get(i).endTime);
    	}
    	
    	// build statement for JDBC
    	String statement = "SELECT courses.course_id_string, courses.course_name_long, courses.start_time, courses.end_time, "
    			+ "courses.monday, courses.tuesday, courses.wednesday, courses.thursday, courses.friday, "
    			+ "professors.name, professors.rating\n"
				+ "FROM mydb.courses\n"
				+ "	LEFT JOIN mydb.professors\n"
				+ "		ON courses.prof_id = professors.prof_id\n"
    			+ "WHERE NOT(1=0";
    	
    	statement = buildStatement(mon, "monday", statement);
    	statement = buildStatement(tue, "tuesday", statement);
    	statement = buildStatement(wed, "wednesday", statement);
    	statement = buildStatement(thu, "thursday", statement);
    	statement = buildStatement(fri, "friday", statement);
    	
    	statement += ")  AND (courses.ge_category = '" + ge + "');";
    	
    	    	
    	// call JDBC Call and store it in arraylist
    	ArrayList<Course> courses = JDBCHandler.searchResultQuery(statement);
		
		// sort based on the rating of professors for each courses
		Collections.sort(courses, Collections.reverseOrder());
		
		// return back to the client as a json
		
		PrintWriter out = response.getWriter();
    	response.setContentType("text/html");
    	response.setCharacterEncoding("UTF-8");
    	out.println("<!doctype html>\n"
    			+ "<html>\n"
    			+ "\n"
    			+ "<head>\n"
    			+ "    <link rel=\"stylesheet\" href=\"style.css\">\n"
    			+ "    <title>Trojan Scheduler</title>\n"
    			+ "    <meta name=\"description\" content=\"Trojan Scheduler helps you build the perfect schedule for any semester at USC\">\n"
    			+ "</head>\n"
    			+ "\n"
    			+ "<body>\n"
    			+ "    <div class=\"results-page\">\n"
    			+ "       <table>\n"
    			+ "        <table width=\"100%\" id=\"result\" bordercolor=\"#cccccc\" cellpadding=\"5\" cellspacing=\"5\">\n"
    			+ "        <tr>\n"
    			+ "            <th>Rating</th>\n"
    			+ "            <th>Class ID</th>\n"
    			+ "            <th>Class Name</th>\n"
    			+ "            <th>Professor Name</th>\n"
    			+ "            <th>Times</th>\n"
    			+ "            <th>Days</th>\n"
    			+ "            <th>GE Type</th>\n"
    			+ "        </tr>\n");
    	for (Course course : courses) {
    		out.println("<tr>");
    		out.println("<td>" + course.rating + "</td>");
    		out.println("<td>" + course.courseID + "</td>");
    		out.println("<td>" + course.courseName + "</td>");
    		out.println("<td>" + course.professor + "</td>");
    		out.println("<td>" + course.startTime + " - " + course.endTime + "</td>");
    		out.println("<td>" + course.day + "</td>");
    		out.println("<td>" + ge + "</td>");
    		out.println("</tr>");
    		
    	}
    	out.println("</table>\n"
    			+ "    </div>\n"
    			+ "</body>\n"
    			+ "\n"
    			+ "</html>");
    	out.flush();
    	
    	
    	
    	
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
