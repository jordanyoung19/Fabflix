import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {
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
        System.out.println("getting to here");

        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");

        System.out.println("new star name: " + starName);
        System.out.println("new birth year: " + birthYear);

        JsonObject responseJsonObject = new JsonObject();

        try  {
            Connection conn = dataSource.getConnection();
            Connection conn_master = dataSource_master.getConnection();
            String maxIdQuery = "select max(id) from stars";
            PreparedStatement finishedMaxQuery = conn.prepareStatement(maxIdQuery);
            ResultSet rs = finishedMaxQuery.executeQuery();
            System.out.println("rs: "+ rs);

            String maxId = "";
            while (rs.next()) {
                maxId = rs.getString("max(id)");
            }

            System.out.println(maxId);
            String newId = "nm" + (Integer.parseInt(maxId.substring(2,maxId.length()))+1);
            System.out.println(newId);
            CallableStatement idStatement = conn_master.prepareCall("call get_new_id(?, ?);");
            idStatement.setString(1, "stars");
            idStatement.registerOutParameter(2, Types.VARCHAR);
            idStatement.executeUpdate();

            String newStarId = idStatement.getString(2);
            System.out.println("new star ID: " + newStarId);

            if (birthYear.length() == 0) {
                String newStarQuery = "insert into stars values (?, ?, null)";
                PreparedStatement noYearQuery = conn_master.prepareStatement(newStarQuery);

                noYearQuery.setString(1, newStarId);
                noYearQuery.setString(2, starName);

                int row = noYearQuery.executeUpdate();
                System.out.println(row);

                responseJsonObject.addProperty("newId", newStarId);
            } else {
                String newStarQuery = "insert into stars values (?, ?, ?)";
                System.out.println("new star query: " + newStarQuery);
                PreparedStatement yearQuery = conn_master.prepareStatement(newStarQuery);

                yearQuery.setString(1, newStarId);
                yearQuery.setString(2, starName);
                yearQuery.setString(3, birthYear);

                System.out.println("new star prepared query: " + yearQuery);

                int row = yearQuery.executeUpdate();
                System.out.println(row);

                responseJsonObject.addProperty("newId", newStarId);
            }

            response.getWriter().write(responseJsonObject.toString());
        }


        catch (Exception e) {
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);
        }


    }
}









