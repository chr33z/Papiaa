package com.elegantwalrus.papersize;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elegantwalrus.papersize.data.FavoriteStore;
import com.elegantwalrus.papersize.data.FormatLoader;
import com.elegantwalrus.papersize.paper.Orientation;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.PaperStandard;
import com.elegantwalrus.papersize.paper.Unit;
import com.elegantwalrus.papersize.sharedpreferences.PaperPreferences_;
import com.elegantwalrus.papersize.utils.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_paper_viewer)
@OptionsMenu(R.menu.menu_paper_viewer)
public class PaperViewerActivity extends ActionBarActivity {

    @Pref
    PaperPreferences_ mPrefs;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.text_paper_size)
    TextView mPaperSize;

    @ViewById(R.id.text_increase_bleed)
    TextView mIncreaseBleed;

    @ViewById(R.id.text_decrease_bleed)
    TextView mDecreaseBleed;

    @ViewById(R.id.text_bleed)
    TextView mLabelBleeding;

    @ViewById(R.id.pager)
    ViewPager mPager;

    private Menu mMenu;

    private ScreenSlidePagerAdapter mPagerAdapter;

    private PaperStandard mStandard;

    private DecimalFormat doubleFormat = new DecimalFormat("#.##");

    private double currentBleeding;

    private float bleedingTouchX;

    private int screenWidth;

    private int maxBleeding = 10;

    @AfterViews
    void onContent() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        mLabelBleeding.setOnTouchListener(bleedingTouchListener);
        mIncreaseBleed.setTypeface(font);
        mDecreaseBleed.setTypeface(font);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        List<PaperStandard> standards = FormatLoader.readPaperFile(this);

        int paperIndex = 0;
        if(getIntent().getStringArrayExtra("format") != null) {
            String[] data = getIntent().getStringArrayExtra("format");
            String standardId = data[0];
            String paperId = data[1];

            for (PaperStandard standard : standards) {
                if(standard.getName().equals(standardId)) {
                    mStandard = standard;

                    for (int i = 0; i < mStandard.getFormats().size(); i++) {
                        if(paperId.equals(mStandard.getFormats().get(i).getName())) {
                            paperIndex = i;
                        }
                    }
                }
            }
        }

        if(mStandard == null) {
            mStandard = standards.get(0);
        }
        getSupportActionBar().setTitle(mStandard.getName());

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mStandard.getFormats());
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(pageChangeListener);
        mPager.setCurrentItem(paperIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        updateControls();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_paper_orientation:
                togglePaperOrientation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
    }

    @OptionsItem(R.id.action_share_paper)
    void onSharePaper() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        View view = fragment.getView();

        if(view != null) {
            Uri fileUri = Utils.saveViewToFile(this, view);

            Intent shareImageIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareImageIntent.setType("image/png");
            shareImageIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

            startActivity(Intent.createChooser(shareImageIntent, getString(R.string.share_intent_chooser)));

        }
    }

    @OptionsItem(R.id.action_favorites)
    void onFavorite() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        Paper paper = fragment.getPaper();

        paper.setFavorite(!paper.isFavorite());
        fragment.invalidate();

        FavoriteStore fs = new FavoriteStore(this);
        try {
            fs.open();
            if(paper.isFavorite()) {
                fs.addFavorite(paper);
            } else {
                fs.deleteFavorite(paper);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fs.close();
        }

        if(paper.isFavorite()) {
            String message = String.format(getString(R.string.toast_favorite_added), paper.getName());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        updateControls();
    }

    @Click(R.id.text_increase_bleed)
    void increasePaperBleed() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        double bleed = fragment.getPaper().getBleed();
        updatePaperBleeding(bleed + 1);
    }

    @Click(R.id.text_decrease_bleed)
    void decreasePaperBleed() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        double bleed = fragment.getPaper().getBleed();
        updatePaperBleeding(bleed - 1);
    }

    void togglePaperOrientation() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        fragment.togglePaperOrientation();

        updateOrientationIcon(fragment.getPaper());
        updatePaperSize();
    }

    private void updateControls() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        maxBleeding = fragment.getPaper().getMaxBleed();

        updatePaperBleeding(fragment.getPaper().getBleed());
        updateOrientationIcon(fragment.getPaper());

        if(mMenu != null) {
            if (fragment.getPaper().isFavorite()) {
                mMenu.getItem(1).setTitle(R.string.action_remove_from_favorites);
                mMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.icon_favorite));
            } else {
                mMenu.getItem(1).setTitle(R.string.action_add_to_favorites);
                mMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.icon_no_favorite));
            }
        }
    }

    private void updatePaperBleeding(double bleed) {
        if(bleed >= 0 && bleed <= maxBleeding) {
            Unit unit = ((PaperApplication)getApplication()).getApplicationUnit();

            if(bleed == 0) {
                mLabelBleeding.setText(
                        getText(R.string.label_add_bleeding));
            } else {
                String bleedInUnit = doubleFormat.format(Unit.fromMillimeter(unit, bleed));
                String formatText = getString(R.string.label_bleeding);
                String text = String.format(Locale.getDefault(), formatText, bleedInUnit, unit.getName());

                mLabelBleeding.setText(text);
            }

            if(bleed <= 0) {
                mIncreaseBleed.setEnabled(true);
                mIncreaseBleed.animate().alpha(1f).start();
                mDecreaseBleed.setEnabled(false);
                mDecreaseBleed.animate().alpha(0.2f).start();
            } else if(bleed >= maxBleeding) {
                mIncreaseBleed.setEnabled(false);
                mIncreaseBleed.animate().alpha(0.2f).start();
                mDecreaseBleed.setEnabled(true);
                mDecreaseBleed.animate().alpha(1f).start();
            } else {
                mIncreaseBleed.setEnabled(true);
                mIncreaseBleed.animate().alpha(1f).start();
                mDecreaseBleed.setEnabled(true);
                mDecreaseBleed.animate().alpha(1f).start();
            }

            PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
            fragment.setBleeding(Unit.MILLIMETER, bleed);

            updatePaperSize();
        }
    }

    private void updateOrientationIcon(Paper paper) {
        if(mMenu != null) {
            if (paper.getOrientation() == Orientation.PORTRAIT) {
                mMenu.getItem(0).setIcon(R.drawable.icon_orientation_portrait);
            } else {
                mMenu.getItem(0).setIcon(R.drawable.icon_orientation_landscape);
            }
        }
    }

    private void updatePaperSize() {
        Paper paper = mPagerAdapter.getFragment(mPager.getCurrentItem()).getPaper();
        Unit unit = ((PaperApplication)getApplication()).getApplicationUnit();
        double width = paper.getWidth() + paper.getBleed() * 2;
        double height = paper.getHeight() + paper.getBleed() * 2;

        String widthInUnit = doubleFormat.format(Unit.fromMillimeter(unit, width));
        String heightInUnit = doubleFormat.format(Unit.fromMillimeter(unit, height));
        mPaperSize.setText(String.format(
                Locale.getDefault(), getString(R.string.format_paper_size), widthInUnit, heightInUnit, unit.getName()));
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            updateControls();
            updatePaperSize();
            mPrefs.lastSelectedFragment().put(position);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private View.OnTouchListener bleedingTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
            Paper paper = fragment.getPaper();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    bleedingTouchX = event.getX();
                    currentBleeding = paper.getBleed();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float difference = x - bleedingTouchX;

                    int maxValue = screenWidth / 2;
                    int stepSize = maxValue / maxBleeding;

                    updatePaperBleeding(currentBleeding + (int) (difference / stepSize));
                    return true;
            }

            return true;
        }
    };

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    protected class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<Paper> mPapers = new ArrayList<>();

        private HashMap<Integer, PaperCanvasFragment> mReferenceMap = new HashMap<>();

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Paper> papers) {
            super(fm);
            this.mPapers = papers;
        }

        @Override
        public Fragment getItem(int position) {
            PaperCanvasFragment canvasFragment = new PaperCanvasFragment();
            canvasFragment.setPaper(mPapers.get(position));
            mReferenceMap.put(position, canvasFragment);
            return canvasFragment;
        }

        @Override
        public int getCount() {
            return mPapers.size();
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mReferenceMap.remove(position);
        }

        public PaperCanvasFragment getFragment(int key) {
            return mReferenceMap.get(key);
        }
    }
}
