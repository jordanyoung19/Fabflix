public class Star {
    public String id;
    public String name;

    public Star(){
        this.id = "";
        this.name = "";
    }
    public Star(String id, String name){
        this.id = id;
        this.name = name;
    }
    void setId (String id){
        this.id = id;
    }
    void setName (String id){
        this.name = name;
    }
    String getId(){
        return this.id;
    }
    String getName(){
        return this.name;
    }

}
