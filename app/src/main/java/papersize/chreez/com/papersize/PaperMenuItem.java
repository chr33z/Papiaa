package papersize.chreez.com.papersize;

import papersize.chreez.com.papersize.paper.Paper;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class PaperMenuItem {

    public String content;
    public Paper paper;
    public int action;
    public int color;

    public PaperMenuItem(String content, Paper paper, int action, int color) {
        this.content = content;
        this.paper = paper;
        this.action = action;
        this.color = color;
    }
}
