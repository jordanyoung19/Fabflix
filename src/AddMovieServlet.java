import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;


    private DataSource dataSource_master;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource_master = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("getting here in add movie servlet");

        String newTitle = request.getParameter("title");
        String newYear = request.getParameter("year");
        String newDirector = request.getParameter("director");
        String newStar = request.getParameter("singleStar");
        String newGenre = request.getParameter("genre");

        System.out.println("testing request parameters here");
        System.out.println(newTitle);
        System.out.println(newYear);
        System.out.println(newDirector);
        System.out.println(newStar);
        System.out.println(newGenre);

        JsonObject responseJsonObject = new JsonObject();

        try {
            Connection conn = dataSource.getConnection();
            Connection connection_master = dataSource.getConnection();

            CallableStatement statement = connection_master.prepareCall("call add_movie(?, ?, ?, ?, ?, ?)");
            statement.setString(1, newTitle);
            statement.setString(2, newYear);
            statement.setString(3, newDirector);
            statement.setString(4, newStar);
            statement.setString(5, newGenre);
            statement.registerOutParameter(6, Types.VARCHAR);
            statement.executeUpdate();

            System.out.println("test");

            String statusResponse = statement.getString(6);
            System.out.println("status message: " + statusResponse);

            responseJsonObject.addProperty("status", statusResponse);

            response.getWriter().write(responseJsonObject.toString());
            response.setStatus(200);
        }

        catch (Exception e) {
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);
        }
    }
}









