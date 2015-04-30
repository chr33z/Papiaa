package papersize.chreez.com.papersize;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import papersize.chreez.com.papersize.data.FormatLoader;
import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperStandard;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @ViewById(R.id.container)
    FrameLayout mListView;

    List<PaperStandard> mStandards;

    @AfterViews
    void onContent() {
        mStandards = FormatLoader.readPaperFile(this);
        openMainMenu();
    }

    private void openMainMenu() {
        MainMenuFragment fragment = new MainMenuFragment_();
        fragment.setData(mStandards);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
    }

    public void openFormatsList(PaperStandard standard) {
        PaperFormatsListFragment fragment = new PaperFormatsListFragment_();
        fragment.setData(standard);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void openPaperViewer(PaperStandard standard, Paper paper) {
        String[] data = {standard.getName(), paper.getName()};
        Intent paperViewerIntent = new Intent(this, PaperViewerActivity_.class);
        paperViewerIntent.putExtra("format", data);
        startActivity(paperViewerIntent);
    }
}
