import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();

        System.out.println("here");

        String username = request.getParameter("username");
        String pass = request.getParameter("password");

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        if (!request.getHeader("User-Agent").toLowerCase().contains("android")) {
            try{
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            }
            catch (Exception e){
                return;
            }
        }
        else{
           System.out.println("app");
        }

        JsonObject responseJsonObject = new JsonObject();
        //check if android for recaptcha
        System.out.println("60");
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("getting to point two");

            String query = "SELECT email, password, id " +
                    "FROM customers";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query);
            boolean success = false;
            while (rs.next()) {
                String dbUsername = rs.getString("email");

                //String Password = rs.getString("password");
                //or pass instead of password?
                String encryptedPassword = rs.getString("password");


                String dbIdNum = rs.getString("id");

                if (dbUsername.equals(username) && new StrongPasswordEncryptor().checkPassword(pass, encryptedPassword)) {
                    success = true;
                    request.getSession().setAttribute("user", new User(username));
                    request.getSession().setAttribute("employee", "false");
                    // adding customer id to user session
                    request.getSession().setAttribute("userId", dbIdNum);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    break;
                }
            }
            System.out.println(username);
            System.out.println(pass);
            System.out.println(success);
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
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
            response.setStatus(200);
        }

        catch (Exception e) {
            System.out.println("exception being caught");
            e.printStackTrace();
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);
        }

    }
}









