

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	
    	PrintWriter pw = response.getWriter();
//        response.setContentType("application/json");
    	response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("username= " + username + " password=" + password);
        Gson gson = new Gson();

        if (username == null || username.isBlank() || password == null || password.isBlank() ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String error = "User info missing";
            pw.write(gson.toJson(error));
            pw.flush();
        } else {
            int userID = JDBCHandler.register(username, password);
            System.out.println("USER ID\n");
            System.out.println(userID);

            if (userID == -1) {
            	response.setStatus(HttpServletResponse.SC_OK);
                pw.write(gson.toJson(userID));
                response.sendRedirect("dashboard.html");  
                pw.flush();
            } else {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String error = "Username not found";
//                response.setContentType("text/html");
                pw.println("<script type=\"text/javascript\">");
                pw.println("alert('Username already exists');");
                pw.println("location='register.html';");
                pw.println("</script>");
//                pw.write(gson.toJson(error));
                pw.flush();  
            }
        }
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
