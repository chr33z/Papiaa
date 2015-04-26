package papersize.chreez.com.papersize;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import papersize.chreez.com.papersize.data.FormatLoader;
import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperStandard;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    MainFragment mMainFragment = new MainFragment_();

    PaperViewerFragment mPaperViewerFragment = new PaperViewerFragment();

    List<PaperStandard> mStandards;

    @AfterViews
    void onContent() {
        mStandards = FormatLoader.readPaperFile(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMainFragment.setData(mStandards);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        getSupportFragmentManager().beginTransaction().add(
                R.id.container, mMainFragment).commit();
    }

    public void openPaperViewer(Paper paper) {
        mPaperViewerFragment.setPaper(paper);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, mPaperViewerFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
