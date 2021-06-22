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

@WebServlet(name = "PayServlet", urlPatterns = "/api/pay")
public class PayServlet extends HttpServlet {
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
        System.out.println("getting here in pay servlet");

        String card = request.getParameter("card");
        String expiration = request.getParameter("expiration");

        System.out.println(card);
        System.out.println(expiration);

        JsonObject responseJsonObject = new JsonObject();

        try {
            Connection conn = dataSource.getConnection();
            Connection conn_master = dataSource_master.getConnection()

            String query = "SELECT id, expiration " +
                    "FROM creditcards";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query);

            boolean success = false;
            while (rs.next()) {
                String dbId = rs.getString("id");
                String dbExpiration = rs.getDate("expiration").toString();

                if (dbId.equals(card) && dbExpiration.equals(expiration)) {
                    success = true;
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                    // handle adding to sql data base

                    HttpSession session = request.getSession();

                    ArrayList<String> items = (ArrayList<String>) session.getAttribute("previousItems");

                    String customerId = (String) session.getAttribute("userId");

                    // order number that will be shown in confirmation page
                    String orderNumber = "";

                    System.out.println("test to get customer ID: " + customerId);
                    System.out.println("test here should print out movie titles");
                    for (int i = 0; i < items.size(); i++) {
                        String movieTitle = items.get(i);

                        Statement state = conn.createStatement();
                        String idQuery = "select id from movies where title = ?";

                        PreparedStatement finishedStatement = conn.prepareStatement(idQuery);

                        finishedStatement.setString(1, movieTitle);

                        System.out.println(finishedStatement);

                        ResultSet res = finishedStatement.executeQuery();

                        System.out.println("test 3");
                        System.out.println(res);

                        // String movieId = res.getString("id");

                        // 140127
                        // 2005-10-26

                        while (res.next()) {
                            System.out.println("res loop");
                            String movieId = res.getString("id");
                            System.out.println("movie title: " + movieTitle);
                            System.out.println("movie ID: " + movieId);

                            orderNumber = orderNumber + "movieId";

                            String pushQuery = "insert into sales (customerId, movieId, saleDate) " +
                                    "values (?, ? ,curdate())";

                            PreparedStatement finishedPushQuery = conn_master.prepareStatement(pushQuery);

                            finishedPushQuery.setString(1, customerId);
                            finishedPushQuery.setString(2, movieId);

                            System.out.println("query: " + finishedPushQuery);

                            int row = finishedPushQuery.executeUpdate();
                            System.out.println(row);

                            System.out.println("getting here");
                        }
                    }

                    // append customerID to beginning to finish order number
                    orderNumber = customerId + orderNumber;

                    responseJsonObject.addProperty("orderNumber", orderNumber);

                    break;
                }
            }
            if (!success) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "please re-enter payment information");
            }

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









