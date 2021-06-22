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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movieList"
@WebServlet(name = "moviesServlet", urlPatterns = "/api/movieList")
public class MovieServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // making another preparedStatement to view in the front end
        PreparedStatement copyStatement = null;

        // Retrieve parameters from url request
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String sortByTitle = request.getParameter("sortByTitle");
        String sortByRating = request.getParameter("sortByRating");

//        System.out.println("sortByT: "+sortByT);
//        System.out.println("sortByR: "+sortByR);

        if (request.getHeader("User-Agent").toLowerCase().contains("android")) {
            System.out.println("all other values are empty string now");
            year = "";
            director = "";
            star = "";
            genre = "";
            sortByTitle = "";
            sortByRating = "";
        }




        // preparing for query
        // prepare title for full text search
        title = title.replace("20", " ");
        String[] titleArray = title.split(" ");
        System.out.println("initial array:" + titleArray);
        for (int i = 0; i < titleArray.length; i++) {
            titleArray[i] = titleArray[i] + '*';
        }
        System.out.println("array after adding *: " + titleArray);
        String titleForQuery = String.join(" ", titleArray);
        System.out.println("title for query: " + titleForQuery);

        title = '%' + title + '%';



            director = director.replace("20", " ");
            director = '%' + director + '%';
            star = star.replace("20", " ");
            star = '%' + star + '%';
            System.out.println("director: " + director);
            System.out.println("star: " + star);


        System.out.println("title: " + title);



        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
//            String query = "select id, title, yr, director, rating, stars, genres from " +
//                    "(select id, title, yr, director, rating, stars, genres from " +
//                    "(select ANY_VALUE(T.id) as id, ANY_VALUE(T.title) as title, ANY_VALUE(T.year) as yr, ANY_VALUE(T.director) as director, ROUND(ANY_VALUE(T.rating),2) as rating, ANY_VALUE(T.stars) as stars, group_concat(G.name) as genres " +
//                    "from (SELECT ANY_VALUE(m.id) as id, ANY_VALUE(m.title) as title, ANY_VALUE(m.year) as year, ANY_VALUE(m.director) as director, ROUND(ANY_VALUE(r.rating),2) as rating, group_concat(s.name) as stars " +
//                    "FROM movies m, ratings r, stars s, stars_in_movies sm " +
//                    "WHERE match(title) against (? in boolean mode) and m.id = r.movieId and s.id=sm.starId and sm.movieId=m.id group by m.title limit 100) as T, genres G, genres_in_movies GM " +
//                    "where T.id = GM.movieId and GM.genreId = G.id group by T.title) as P " +
//                    "order by rating DESC) as R ";

            // attempting optimized query
            // NEED WORK: get stars from this query as well
//            String query = "with temp_movies as " +
//                    "(select * from movies where match(title) against (? in boolean mode) limit 100) " +
//                    "select distinct m.id as mId, m.title as mTitle, m.year mYear, m.director as mDirector, r.rating as mRating from temp_movies as m " +
//                    "inner join stars_in_movies as sm on m.id = sm.movieId inner join stars as s on sm.starId = s.id " +
//                    "left join ratings as r on m.id = r.movieId " +
//                    "where m.id = m.id";

            // another attempt at optimized query
            String query = "with temp_movies as " +
                    "(select * from movies where match(title) against (? in boolean mode) limit 100) " +
                    "select distinct any_value(m.id) as id, any_value(m.title) as title, any_value(m.year) as yr, any_value(m.director) as director, ROUND(any_value(r.rating),2) as rating, group_concat(s.name) as stars, group_concat(distinct g.name) as genres " +
                    "from temp_movies as m " +
                    "inner join stars_in_movies as sm on m.id = sm.movieId " +
                    "inner join stars as s on sm.starId = s.id " +
                    "inner join genres_in_movies as gm on m.id = gm.movieId " +
                    "inner join genres as g on g.id = gm.genreId " +
                    "left join ratings as r on m.id = r.movieId " +
                    "where m.id = m.id";

            PreparedStatement statement;


            //IF SEARCHING, PERFORM SEARCH QUERY
            if (genre.length()==4 || request.getHeader("User-Agent").toLowerCase().contains("android")) {
//                System.out.println("WE ARE SEARCHING");
                // NOTE FOR PROJ 4: This is most likely where the full text search will be implemented
//                query += " where title like ? and director like ? and stars like ?";
                query += " and m.director like ? and s.name like ?";

                if (year.length() > 0){
                    query += " and yr = " + year;
                }

                // adding this for full text query
                query += " group by m.title";

                //sort by asc/desc title or rating
                if (sortByTitle.equals("asc") || sortByTitle.equals("desc")) {
                    System.out.println("sorting by title");
                    if (sortByTitle.equals("asc")) {
                        System.out.println("sorting by asc title");
                        query += " order by title ASC";
                    }
                    else {
                        System.out.println("sorting by title");
                        query += " order by title DESC";
                    }
                }
                else if (sortByRating.equals("asc") || sortByRating.equals("desc")) {
                    System.out.println("sorting by Rating");
                    if (sortByRating.equals("asc")) {
                        System.out.println("sorting by asc rating");
                        query += " order by rating ASC";
                    }
                    else {
                        System.out.println("sorting by desc rating");
                        query += " order by rating DESC";
                    }
                }

                System.out.println("new query with match:" + query);
                statement = conn.prepareStatement(query);

                statement.setString(1, titleForQuery);
                statement.setString(2, director);
                statement.setString(3, star);

                System.out.println("prepared statement with match: " + statement);
                copyStatement = statement;
            }

            //IF BROWSING BY TITLE, PERFORM CORRESPONDING QUERY
            else if (genre.isEmpty()){
                System.out.println("browsing by title");

                //if they press *, show all movies that start with non-alphanumeric characters
                if (title.equals("%*%")){
                    query += " and title REGEXP '^[^a-zA-Z0-9]'";
                    // adding this because of full text search
                    query += " group by m.title";
                    //sort by asc/desc title or rating
                    if (sortByTitle.equals("asc") || sortByTitle.equals("desc")) {
                        System.out.println("sorting by title");
                        if (sortByTitle.equals("asc")) {
                            System.out.println("sorting by asc title");
                            query += " order by title ASC";
                        }
                        else {
                            System.out.println("sorting by title");
                            query += " order by title DESC";
                        }
                    }
                    else if (sortByRating.equals("asc") || sortByRating.equals("desc")) {
                        System.out.println("sorting by Rating");
                        if (sortByRating.equals("asc")) {
                            System.out.println("sorting by asc rating");
                            query += " order by rating ASC";
                        }
                        else {
                            System.out.println("sorting by desc rating");
                            query += " order by rating DESC";
                        }
                    }
                    statement = conn.prepareStatement(query);

                    // adding this for figuring out AWS Bug
                    copyStatement = statement;
                }

                //if not clicked on *, perform corresponding query
                else{
                    query = query.replace("match(title) against (? in boolean mode)", "title like '%%'");
                    title = title.substring(1);
                    query += " and title like ?";
                    // adding this because of full text search
                    query += " group by m.title";
                    //sort by asc/desc title or rating
                    if (sortByTitle.equals("asc") || sortByTitle.equals("desc")) {
                        System.out.println("sorting by title");
                        if (sortByTitle.equals("asc")) {
                            System.out.println("sorting by asc title");
                            query += " order by title ASC";
                        }
                        else {
                            System.out.println("sorting by title");
                            query += " order by title DESC";
                        }
                    }
                    else if (sortByRating.equals("asc") || sortByRating.equals("desc")) {
                        System.out.println("sorting by Rating");
                        if (sortByRating.equals("asc")) {
                            System.out.println("sorting by asc rating");
                            query += " order by rating ASC";
                        }
                        else {
                            System.out.println("sorting by desc rating");
                            query += " order by rating DESC";
                        }
                    }
                    statement = conn.prepareStatement(query);
                    statement.setString(1, title);

                    System.out.println("changed query: " + statement);

                    // adding this to try and figure out AWS bug
                    copyStatement = statement;
                }

            }

            //IF BROWSING BY GENRE, PERFORM CORRESPONDING QUERY
            else{
                System.out.println("browsing by genre");
                genre = '%' + genre + '%';
//                query += " where genres like ?";

                query = query.replace("match(title) against (? in boolean mode)", "title like '%%'");
                // adding this because of full text search
                query += " and g.name like ?";
                query += " group by m.title";

                //sort by asc/desc title or rating
                if (sortByTitle.equals("asc") || sortByTitle.equals("desc")) {
                    System.out.println("sorting by title");
                    if (sortByTitle.equals("asc")) {
                        System.out.println("sorting by asc title");
                        query += " order by title ASC";
                    }
                    else {
                        System.out.println("sorting by title");
                        query += " order by title DESC";
                    }
                }
                else if (sortByRating.equals("asc") || sortByRating.equals("desc")) {
                    System.out.println("sorting by Rating");
                    if (sortByRating.equals("asc")) {
                        System.out.println("sorting by asc rating");
                        query += " order by rating ASC";
                    }
                    else {
                        System.out.println("sorting by desc rating");
                        query += " order by rating DESC";
                    }
                }

                statement = conn.prepareStatement(query);

                // statement.setString(1, "%%");
                statement.setString(1, genre);

                System.out.println("testing genre browse");
                System.out.println("prepared statement: " + statement);

                // adding this to try and figure out AWS bug
                copyStatement = statement;
            }

            System.out.println("made it here");
            System.out.println("prepared query: " + statement);

            ResultSet rs = statement.executeQuery();

            System.out.println("statement finished executing");

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("yr");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_stars = rs.getString("stars");
                String movie_genres = rs.getString("genres");
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_genres", movie_genres);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();
            out.write(jsonArray.toString());
            response.setStatus(200);

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            jsonObject.addProperty("errorCause", String.valueOf(e.getCause()));
            jsonObject.addProperty("sqlQuery" , copyStatement.toString());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        finally {
            out.close();
        }

    }
}
