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

@WebServlet(name = "editItemsServlet", urlPatterns = "/api/editItems")
public class editItemsServlet extends HttpServlet {
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
        String movieTitle = request.getParameter("title");

        HttpSession session = request.getSession();

        System.out.println("Test: " + movieTitle);

        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> newItems = new ArrayList<String>();

//        if (previousItems == null) {
//            previousItems = new ArrayList<String>();
//            previousItems.add(movieTitle);
//            session.setAttribute("previousItems", previousItems);
//        } else {
//            synchronized (previousItems){
//                previousItems.add(movieTitle);
//            }
//        }

        for (int i = 0; i < previousItems.size(); i++) {
            String tempTitle = previousItems.get(i);
            if (!tempTitle.equals(movieTitle)) {
                newItems.add(tempTitle);
            }
        }

        session.setAttribute("previousItems", newItems);

        System.out.println("getting to here");

//        JsonArray previousItemsArray = new JsonArray();
//        previousItems.forEach(previousItemsArray::add);
//
//        responseJsonObject.add("previousItems", previousItemsArray);
//
//        response.getWriter().write(responseJsonObject.toString());
    }
}









