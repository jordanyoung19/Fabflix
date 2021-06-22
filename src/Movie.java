public class Movie {
    public String title, director, id;
    public int year;


    public Movie(){
        this.id = "";
        this.title = "";
        this.year = 0;
        this.director = "";
    }
    public Movie(String id, String title, String director, int year){
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }

    String getId(){ return this.id; }
    String getTitle(){
        return this.title;
    }
    int getYear(){
        return this.year;
    }
    String getDirector(){
        return this.director;
    }

    void setId(String id){
        this.id = id;
    }
    void setTitle(String title){
        this.title = title;
    }
    void setYear(int year){
        this.year = year;
    }
    void setDirector(String director){
        this.director = director;
    }
}