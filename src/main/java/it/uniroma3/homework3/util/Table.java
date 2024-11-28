package it.uniroma3.homework3.util;

public class Table {
    private String id;
    private String caption;
    private String table;
    private String tableInfo;
    private String footnotes;
    private String references;

    public Table(String id, String caption, String table, String tableInfo, String footnotes, String references) {
        this.id = id;
        this.caption = caption;
        this.table = table;
        this.tableInfo = tableInfo;
        this.footnotes = footnotes;
        this.references = references;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }

    public String getTableInfo() { return tableInfo; }
    public void setTableInfo(String tableInfo) { this.tableInfo = tableInfo; }

    public String getFootnotes() { return footnotes; }
    public void setFootnotes(String footnotes) { this.footnotes = footnotes; }

    public String getReferences() { return references; }
    public void setReferences(String references) { this.references = references; }

    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                ", Caption='" + caption + '\'' +
                //", Table='" + tabla + '\'' +
                //", tableInfo='" + tableInfo + '\'' +
                //", Footnotes='" + footnotes + '\'' +
                //", References='" + references + '\'' +
                "}";
    }
}
