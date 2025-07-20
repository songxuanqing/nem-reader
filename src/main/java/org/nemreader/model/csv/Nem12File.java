package org.nemreader.model.csv;
import java.util.List;

public class Nem12File {
    private Nem12Header100 header;
    private List<Nem12NMIDataDetails200> data;
    private Nem12End900 footer;

    public Nem12File(Nem12Header100 header, List<Nem12NMIDataDetails200> data, Nem12End900 footer) {
        this.header = header;
        this.data = data;
        this.footer = footer;
    }

    public Nem12Header100 getHeader() {
        return header;
    }

    public void setHeader(Nem12Header100 header) {
        this.header = header;
    }

    public List<Nem12NMIDataDetails200> getData() {
        return data;
    }

    public void setData(List<Nem12NMIDataDetails200> data) {
        this.data = data;
    }

    public Nem12End900 getFooter() {
        return footer;
    }

    public void setFooter(Nem12End900 footer) {
        this.footer = footer;
    }
}