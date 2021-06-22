import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class RelationParser {
    List<Star> SList = new ArrayList<>();List<List<String>> al = new ArrayList<>();List<Movie> MList = new ArrayList<>();
    Document dom;
    HashSet<String> Casts = new HashSet<>();HashMap<String, String> MOvies = new HashMap<>();HashMap<String, String> ACtors = new HashMap<>();


    private String retrievewords(Element e, String s) {
        String text = "";
        NodeList nodeList = e.getElementsByTagName(s);
        int len = nodeList.getLength();
        while(nodeList!=null) { if (len>0){ Element element = (Element) nodeList.item(0);text = element.getFirstChild().getNodeValue(); } }
        return text; }

    private void parseDocument() {
        Element element = dom.getDocumentElement();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password");
            NodeList nodelist = element.getElementsByTagName("m");dbcon.setAutoCommit(false);

            String query = "SELECT max(id) " +
                    "FROM movies";
            PreparedStatement statement = dbcon.prepareStatement(query);ResultSet result = statement.executeQuery();
            int mid = Integer.parseInt(result.getString("id"));

            String query1 = "SELECT max(id)" +
                    " FROM stars";
            PreparedStatement statement1 = dbcon.prepareStatement(query1);ResultSet result1 = statement1.executeQuery();
            int sid = Integer.parseInt(result1.getString("id"));

            String query2 = "SELECT * " +
                    "FROM movies; movies";
            ResultSet rs = dbcon.createStatement().executeQuery(query2);MOvies.put(rs.getString("title"), rs.getString("id"));


            query2 = "SELECT * from stars";
            ResultSet rs1 = dbcon.createStatement().executeQuery(query2);ACtors.put(rs1.getString("name"), rs1.getString("id"));


            query2 = "SELECT * from stars_in_movies";
            ResultSet rs2 = dbcon.createStatement().executeQuery(query2);Casts.add(rs2.getString("starId") + rs2.getString("movieId"));
            int i;
            int len = nodelist.getLength();
            for(i=0; i<len; ++i) {
                    Element cast = (Element) nodelist.item(i);
                    String Title, Sid, Name, Mid; Title= retrievewords(cast, "t");Name= retrievewords(cast, "a");
                    if(MOvies.containsKey(Title)==false){ Mid = Integer.toString(mid);MOvies.put(Title, Mid); }
                    else{ Mid = MOvies.get(Title);}
                    Sid = ACtors.get(Name);
                    if(!ACtors.containsKey(Name)) { Sid = Integer.toString(sid) ;Star tmpStar = new Star();ACtors.put(Name, Sid); }
            }


            String query5 = "INSERT INTO stars_in_movies " +
                    "VALUES(?, ?)";
            PreparedStatement st5 = dbcon.prepareStatement(query5);
            int m;
            int lenn = al.size();
            for(m = 0; m <lenn; ++m) {
                st5.setString(1, al.get(m).get(0));st5.setString(2, al.get(m).get(1));
                st5.addBatch();
            }
            st5.executeBatch();dbcon.commit();
            String query3 = "INSERT INTO stars " +
                    "VALUES(?, ?, ?)";
            int j ;
            PreparedStatement st3 = dbcon.prepareStatement(query3);
            for(j=0; j< SList.size(); j++) {
                st3.setString(1, SList.get(i).getName());st3.setString(2, SList.get(i).getId());st3.setNull(3, Types.INTEGER);
                st3.addBatch();
            }
            st3.executeBatch();dbcon.commit();

            String query4 = "INSERT INTO movies VALUES(?, ?, ?, ?)";
            PreparedStatement st4 = dbcon.prepareStatement(query4);
            int k;
            int mllen = MList.size();
            for(k = 0; k < mllen; ++k) {
                st4.setString(1, MList.get(k).getTitle());st4.setString(2, MList.get(k).getId());st4.setInt(3, MList.get(k).getYear());st4.setString(4, MList.get(k).getDirector());
                st4.addBatch();
            }
            st4.executeBatch();dbcon.commit();
            dbcon.close(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void runRelationParser(){
        parseXML();
        parseDocument();
    }

    private void parseXML() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("casts124.xml");
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

    public static void main(String[] args) {
        RelationParser parse = new RelationParser();
        parse.runRelationParser();
    }}
