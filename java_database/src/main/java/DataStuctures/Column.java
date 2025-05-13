package DataStuctures;

public class Column {

    private final String name;
    private String data;

    public Column(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getData() {
        return data;
    }
    public String getName() {
        return name;
    }
    public void setData(String newData) { data = newData; }

}
