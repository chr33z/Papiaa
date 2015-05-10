package com.elegantwalrus.papersize;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.elegantwalrus.papersize.data.FormatLoader;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.PaperStandard;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends ActionBarActivity {

    @ViewById(R.id.container_left)
    FrameLayout mListStandard;

    @ViewById(R.id.container_right)
    FrameLayout mListFormat;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    List<PaperStandard> mStandards;

    PaperStandard mCurrentStandard;

    MainMenuFragment mainMenuFragment;

    private boolean isExpanded = false;

    @AfterViews
    void onContent() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        mStandards = FormatLoader.readPaperFile(this);
        openMainMenu();
    }

    private void openMainMenu() {
        mainMenuFragment = new MainMenuFragment_();
        mainMenuFragment.setData(mStandards);
        getSupportFragmentManager().beginTransaction().add(R.id.container_left, mainMenuFragment).commit();
    }

    public void openFormatsList(PaperStandard standard) {
        if(!standard.equals(mCurrentStandard)) {
            mCurrentStandard = standard;

            PaperFormatsListFragment fragment = new PaperFormatsListFragment_();
            fragment.setData(standard);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_right, fragment);
            ft.commit();

            if(!isExpanded) {
                expandList();
            }
        }
    }

    public void openPaperViewer(PaperStandard standard, Paper paper) {
        String[] data = {standard.getName(), paper.getName()};
        Intent paperViewerIntent = new Intent(this, PaperViewerActivity_.class);
        paperViewerIntent.putExtra("format", data);
        startActivity(paperViewerIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    @Override
    public void onBackPressed() {
        if(mCurrentStandard != null) {
            collapseList();
            mainMenuFragment.resetButtons();
            mCurrentStandard = null;
        } else {
            super.onBackPressed();
        }
    }

    private void expandList() {
        ExpandAnimation animation = new ExpandAnimation(0.0f, 1.0f);
        animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        mListFormat.startAnimation(animation);

        isExpanded = true;
    }

    private void collapseList() {
        ExpandAnimation animation = new ExpandAnimation(1.0f, 0.0f);
        animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        mListFormat.startAnimation(animation);

        isExpanded = false;
    }

    private class ExpandAnimation extends Animation {

        private final float mStartWeight;
        private final float mDeltaWeight;

        public ExpandAnimation(float startWeight, float endWeight) {
            mStartWeight = startWeight;
            mDeltaWeight = endWeight - startWeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mListFormat.getLayoutParams();
            lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
            mListFormat.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    @OptionsItem(R.id.action_about)
    void onAbout() {
        AboutActivity_.intent(this).start();
    }
}
