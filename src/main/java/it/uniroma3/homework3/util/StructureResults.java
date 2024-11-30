package it.uniroma3.homework3.util;

public class StructureResults {
    private String docName;
    private String id;
    private String caption;
    private String mark;
    private String table;

    public StructureResults(String docName, String id, String caption, String mark, String table) {
        this.docName = docName;
        this.id = id;
        this.caption = caption;
        this.mark = mark;
        this.table = table;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }


    @Override
    public String toString() {
        return "DocName='" + docName + '\'' +
                ", id='" + id + '\'' +
                ", Caption='" + caption + '\'' +
                ", Mark='" + mark + '\'' +
                ", Table='" + table + '\'';
    }
}

