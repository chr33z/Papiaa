package papersize.chreez.com.papersize.paper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class PaperSeries {

    private String name = "";

    private String description = "";

    private List<Paper> formats = new ArrayList<>();

    public PaperSeries(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addFormat(Paper format) {
        formats.add(format);
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public List<Paper> getFormats() {
        return formats;
    }
}
