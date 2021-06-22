
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet(name = "movieSuggestionServlet", urlPatterns = "/api/movieSuggestion")
public class movieSuggestionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    public movieSuggestionServlet() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String autoQuery = request.getParameter("query");

            System.out.println("query for autocomplete: " + autoQuery);

            // return the empty json array if query is null or empty
            if (autoQuery == null || autoQuery.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars


            String autoCompleteQuery = "Select id, title from movies where match(title) against (? in boolean mode) limit 10";

            String[] autoQueryArray = autoQuery.split(" ");
            System.out.println("initial array:" + autoQueryArray);
            for (int i = 0; i < autoQueryArray.length; i++) {
                autoQueryArray[i] = '+' + autoQueryArray[i] + '*';
            }
            System.out.println("array after adding *: " + autoQueryArray);
            String autoForQuery = String.join(" ", autoQueryArray);
            System.out.println("title for query: " + autoForQuery);

            Connection conn = dataSource.getConnection();
            PreparedStatement prepStatement = conn.prepareStatement(autoCompleteQuery);

            prepStatement.setString(1, autoForQuery);

            ResultSet rs = prepStatement.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");

                System.out.println("autocomplete suggestion movieID: " + id);
                System.out.println("autocomplete suggestion movieTitle: " + title);

                jsonArray.add(generateJsonObject(id, title));
            }

            // this was from example project
//            for (Integer id : superHeroMap.keySet()) {
//                String heroName = superHeroMap.get(id);
//                if (heroName.toLowerCase().contains(query.toLowerCase())) {
//                    jsonArray.add(generateJsonObject(id, heroName));
//                }
//            }

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String id, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", id);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
