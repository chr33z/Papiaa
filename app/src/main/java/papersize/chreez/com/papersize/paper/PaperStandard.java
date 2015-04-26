package papersize.chreez.com.papersize.paper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class PaperStandard {

    private String name = "";

    private String description = "";

    private List<PaperSeries> serieses = new ArrayList<>();

    public PaperStandard(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addSeries(PaperSeries series) {
        serieses.add(series);
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public List<PaperSeries> getSeries() {
        return serieses;
    }

    public PaperSeries getSeries(String id) {
        for (PaperSeries series : serieses) {
            if(series.getName().equals(id)) {
                return series;
            }
        }
        return null;
    }
}
