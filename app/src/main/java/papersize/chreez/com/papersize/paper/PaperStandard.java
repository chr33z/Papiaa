package papersize.chreez.com.papersize.paper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class PaperStandard {

    private String name = "";

    private String description = "";

    private List<Paper> papers = new ArrayList<>();

    public PaperStandard(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addPaper(Paper paper) {
        papers.add(paper);
    }

    public String getName() {
        return name != null ? name : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public List<Paper> getFormats() {
        return papers;
    }

    public Paper getFormat(String id) {
        for (Paper paper : papers) {
            if(paper.getName().equals(id)) {
                return paper;
            }
        }
        return null;
    }
}
