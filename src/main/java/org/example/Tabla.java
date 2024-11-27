package org.example;

class Tabla {
    private String id;
    private String caption;
    private String tabla;
    private String informacionTabla;
    private String footnotes;
    private String referencias;

    public Tabla(String id, String caption, String tabla, String informacionTabla, String footnotes, String referencias) {
        this.id = id;
        this.caption = caption;
        this.tabla = tabla;
        this.informacionTabla = informacionTabla;
        this.footnotes = footnotes;
        this.referencias = referencias;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getTabla() { return tabla; }
    public void setTabla(String tabla) { this.tabla = tabla; }

    public String getInformacionTabla() { return informacionTabla; }
    public void setInformacionTabla(String informacionTabla) { this.informacionTabla = informacionTabla; }

    public String getFootnotes() { return footnotes; }
    public void setFootnotes(String footnotes) { this.footnotes = footnotes; }

    public String getReferencias() { return referencias; }
    public void setReferencias(String referencias) { this.referencias = referencias; }

    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                ", Caption='" + caption + '\'' +
                //", Table='" + tabla + '\'' +
                //", InformacionTabla='" + informacionTabla + '\'' +
                //", Footnotes='" + footnotes + '\'' +
                //", Referencias='" + referencias + '\'' +
                '}';
    }
}

