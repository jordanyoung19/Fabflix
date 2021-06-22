import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;

public class StarParser{
    Document dom;
    private void parseXML() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse("actors63.xml");
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

    public void runStarParser(){
        parseXML();
        parseDocument();
    }

    private void parseDocument() {
        Element element = dom.getDocumentElement();
        try {
            PreparedStatement st = null;
            Class.forName("com.mysql.jdbc.Driver");Connection db = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password");
            NodeList nodelist = element.getElementsByTagName("actor");

            String qu = "INSERT INTO stars " +
                    "VALUES(?,?,?)";

            try { st = db.prepareStatement(qu);db.setAutoCommit(false); }
            catch (Exception e) { e.printStackTrace(); }

            if(nodelist!=null) {
                int len = nodelist.getLength();
                int i=0;
                while(i<len) {
                    int j;
                    for(j=0; j<len; ++j) {
                        Element star = (Element) nodelist.item(i);String starname = retrievewords("stagename",star);String year = retrievewords("dob",star);

                        String query = "SELECT * FROM stars " +
                                "WHERE name=? and birthYear=?";
                        PreparedStatement statement = db.prepareStatement(query);statement.setString(1, year);statement.setString(2, starname);ResultSet rs = statement.executeQuery();

                        if(!rs.next()) {
                            String query1 = "SELECT MAX(id) as maxId " +
                                    "FROM stars";
                            PreparedStatement statement2 = db.prepareStatement(query1);ResultSet rs1 = statement2.executeQuery();
                            String id = "";
                            if(rs.next()) { String idd = rs1.getString("maxId");id = idd; }
                            st.setString(1, starname);st.setString(2, year);st.setString(3, id);
                            st.addBatch(); }i+=1;
                    }
                    st.executeBatch();db.commit();st.clearBatch(); } }db.close();
        } catch (Exception e) { e.printStackTrace(); } }

    private String retrievewords(String m,Element el) {
        NodeList nodelist = el.getElementsByTagName(m);
        String text = "";
        if(nodelist!=null) { Element e = (Element) nodelist.item(0);text = e.getFirstChild().getNodeValue(); }
        return text; }


    public static void main(String[] args) {
        StarParser parse = new StarParser();
        parse.runStarParser();
    }}