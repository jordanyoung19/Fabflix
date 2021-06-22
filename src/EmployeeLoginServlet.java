import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employeeLogin")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }

        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        System.out.println("Username: " + username);
        System.out.println("pass: " + pass);

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {

            Statement statement = conn.createStatement();

            String query = "SELECT email, password " +
                    "FROM employees";
            ResultSet rs = statement.executeQuery(query);

            System.out.println(rs);

            boolean success = false;
            while (rs.next()) {
                String dbUsername = rs.getString("email");
                String dbPassword = rs.getString("password");

                if (dbUsername.equals(username) && dbPassword.equals(pass)) {
                    success = true;
                    request.getSession().setAttribute("user", new User(username));
                    request.getSession().setAttribute("employee", "true");
                    // adding customer id to user session
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    break;
                }
            }
            if (!success) {
                if(username.equals("") || pass.equals("")) {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "please enter both username and password!");
                }
                else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect username or password");
                }
            }
            System.out.println(responseJsonObject.toString());

            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
            response.setStatus(200);
        }

        catch (Exception e) {
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);
        }
    }
}









