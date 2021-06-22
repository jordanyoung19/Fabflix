import java.sql.Connection;
import java.sql.*;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;

public class MovieParser {
    String mid ="";
    Document dom;

    private void parseXML() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("mains243.xml");
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runMovieParser(){
        parseXML();
        parseDocument();
    }

    private void newgenres(Connection dbcon, String genre, ResultSet genres, ResultSet movies) throws SQLException {
        mid = movies.getString("id");
        if(!genres.next()) {
            String query = "SELECT MAX(id) as maxGenreId " +
                    "FROM genres";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            int gId = result.getInt("maxGenreId");

            String query1 = "INSERT INTO genres " +
                    "VALUES(?,?)";
            PreparedStatement statement1 = dbcon.prepareStatement(query1);statement1.setString(1, genre);statement1.setInt(2, gId);statement1.executeUpdate();

            query1 += "INSERT INTO genres_in_movies " +
                    "VALUES(?, ?)";
            PreparedStatement statement2 = dbcon.prepareStatement(query1);statement2.setString(1, mid);statement2.setInt(2, gId);statement2.executeUpdate();
        }
        else if(genres.next()==true) {
            String query2 = "INSERT INTO genres_in_movies " +
                    "VALUES(?,?)";
            PreparedStatement statement3 = dbcon.prepareStatement(query2);
            int gID = genres.getInt("id");
            statement3.setString(1, mid);statement3.setInt(2, gID);statement3.executeUpdate();
        } }

    private boolean newMovies(Connection dbcon, String dir, String year, String title, String id) throws SQLException {
        boolean result = false;
        String query = "SELECT * FROM movies " +
                "WHERE title=? AND " +
                "YEAR = ?";
        PreparedStatement statement = dbcon.prepareStatement(query);
        statement.setString(1, year);statement.setString(2, title);ResultSet rs = statement.executeQuery();
        while(!rs.next()) {
            result = true;
            String query1 = "INSERT INTO ratings VALUE(?,?,?)";
            PreparedStatement statement2 = dbcon.prepareStatement(query1);statement2.setString(1, id);statement2.executeUpdate();

            String query2 = "INSERT INTO movies VALUE(?, ?, ?, ?)";
            PreparedStatement statement3 = dbcon.prepareStatement(query2);statement3.setString(1, id);statement3.setString(2, title);statement3.setString(3, year);statement3.setString(4, dir);statement3.executeUpdate();
        }
        return result;
    }

    private void parseDocument() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password");Element element = dom.getDocumentElement();
            NodeList nodelist = element.getElementsByTagName("directorfilms");

            if(nodelist!=null) {
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Element dirfil = (Element) nodelist.item(i);
                    String dir = retrievewords(dirfil, "dirname");
                    NodeList movies = dirfil.getElementsByTagName("film");
                    int ml = movies.getLength();
                    int j;
                    for (j=0; j < ml; ++j) {
                        String movie_id = ""; String title, year, genre;
                        Element film = (Element) movies.item(j);
                        title = retrievewords(film, "t");year = retrievewords(film, "year");genre = retrievewords(film, "cat");
                        String query = "SELECT MAX(id) as mid " +
                                "from movies";
                        PreparedStatement st = dbcon.prepareStatement(query);ResultSet rs = st.executeQuery();
                        while(rs.next()) { String mov = rs.getString("mid");movie_id = mov;

                            if(newMovies(dbcon, dir, year, title, movie_id)==true) {
                                String query1 = "SELECT * " +
                                        "FROM genres " +
                                        "WHERE name=?";
                                PreparedStatement st1 = dbcon.prepareStatement(query1);st1.setString(1, genre);ResultSet rs1 = st1.executeQuery();

                                String query2 = "SELECT * " +
                                        "FROM movies " +
                                        "WHERE title=? and director=?";
                                PreparedStatement st2 = dbcon.prepareStatement(query2);st2.setString(1, title);st2.setString(2, dir);ResultSet rs2 = st2.executeQuery();
                                newgenres(dbcon, genre, rs1, rs2);
                            } } } }
            }
            dbcon.close();
        }
        catch(Exception e) { e.printStackTrace(); } }

    private String retrievewords(Element e, String s) {
        String text = "";
        NodeList nodeList = e.getElementsByTagName(s);
        int len = nodeList.getLength();
        while(nodeList!=null) { if (len>0){ Element element = (Element) nodeList.item(0);text = element.getFirstChild().getNodeValue(); } }
        return text; }


    public static void main(String[] args) {
        MovieParser parse = new MovieParser();
        parse.runMovieParser();
    }
}
