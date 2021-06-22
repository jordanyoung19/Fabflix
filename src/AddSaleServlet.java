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

@WebServlet(name = "AddSaleServlet", urlPatterns = "/api/addSale")
public class AddSaleServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        // session.getAttribute("previousItems") is an arrayList

        ArrayList<String> items = (ArrayList<String>) session.getAttribute("previousItems");

        JsonArray previousItemsArray = new JsonArray();
        items.forEach(previousItemsArray::add);

        PrintWriter out = response.getWriter();

        out.write(previousItemsArray.toString());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int movieQuantity = 1;
        int moviePrice = 10;

        System.out.println("getting to here");

        HttpSession session = request.getSession();

        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        JsonArray previousItemsArray = new JsonArray();
        previousItems.forEach(previousItemsArray::add);

        responseJsonObject.add("previousItems", previousItemsArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}









