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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_paper_viewer)
@OptionsMenu(R.menu.menu_paper_viewer)
public class PaperViewerActivity extends ActionBarActivity {

    private static final int ROTATION_DEGREE = 5;

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

    private double currentBleeding;

    private float bleedingTouchX;

    private int screenWidth;

    private int maxBleeding = 10;

    private Unit unit = Unit.MILLIMETER;

    private static final float thresholdOffset = 0.1f;

    private boolean scrollStarted;

    private boolean checkDirection;

    @AfterViews
    void onContent() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        unit = ((PaperApplication)getApplication()).getApplicationUnit();

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
        fs.open();
        if(paper.isFavorite()) {
            fs.addFavorite(paper);
        } else {
            fs.deleteFavorite(paper);
        }
        fs.close();

        if(paper.isFavorite()) {
            String message = String.format(getString(R.string.toast_favorite_added), paper.getName());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        updateControls();
    }

    @OptionsItem(R.id.action_paper_to_pixel)
    void onPaperDimensionsInPixels() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        Paper paper = fragment.getPaper();
        Unit unit = ((PaperApplication)getApplication()).getApplicationUnit();

        new PixelDimensionsDialog(this, paper, unit).show();
    }

    @Click(R.id.text_increase_bleed)
    void increasePaperBleed() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        double bleed = fragment.getPaper().getBleed();
        updatePaperBleeding(bleed + unit.getBleedStep());
    }

    @Click(R.id.text_decrease_bleed)
    void decreasePaperBleed() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        double bleed = fragment.getPaper().getBleed();
        updatePaperBleeding(bleed - unit.getBleedStep());
    }

    private void togglePaperOrientation() {
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
        // bring bleed to min value
        bleed = bleed < 0 ? 0 : bleed;

        if(bleed >= 0 && bleed <= maxBleeding) {
            if(bleed <= 0) {
                mLabelBleeding.setText(getText(R.string.label_add_bleeding));
            } else {
                DecimalFormat df = unit == Unit.INCH ? new DecimalFormat("0.000") : new DecimalFormat("0.0");
                String bleedInUnit = df.format(Unit.fromMillimeter(unit, bleed));
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

        DecimalFormat df = unit == Unit.INCH ? new DecimalFormat("0.00") : new DecimalFormat("0.#");
        String widthInUnit = df.format(Math.round(Unit.fromMillimeter(unit, width) * 20) / 20.0);
        String heightInUnit = df.format(Math.round(Unit.fromMillimeter(unit, height) * 20) / 20.0);
        mPaperSize.setText(String.format(
                Locale.getDefault(), getString(R.string.format_paper_size), widthInUnit, heightInUnit, unit.getName()));
    }

    private void updatePageScaling(int position, int direction, float scale) {
        /*
            When swiping to left, position is the currently visible page.
            When swiping to right, position is the page that comes into view!

            When direction is +1 then scale is from 0-1, otherwise its from 1-0... D'oh!
         */

        if(direction == 0) {
            // skip when there was not swipe (when first fragment is loaded)
            return;
        } else if(direction < 0) {
            // normalize scale to 0-1
            scale = 1 - scale;
        }

        PaperCanvasFragment paperScaleOut = mPagerAdapter.getFragment(direction > 0 ? position : position + 1);
        PaperCanvasFragment paperScaleIn = mPagerAdapter.getFragment(direction > 0 ? position + 1 : position);

        if(paperScaleIn != null && paperScaleOut != null) {
            // The ration between two papers
            float scalePapers = 0.3f;

            /*
                Calculate ration between two paper sizes to determine how much to scale them
             */
            Paper paperOut = mPagerAdapter.getFragment(direction > 0 ? position : position + 1).getPaper();
            Paper paperIn = mPagerAdapter.getFragment(direction > 0 ? position + 1 : position).getPaper();
            if(paperOut != null && paperIn != null) {
                if(direction > 0) {
                    scalePapers = (float) (1 - paperIn.getHeight() / paperOut.getHeight());
                } else {
                    scalePapers = (float) (1 - paperOut.getHeight() / paperIn.getHeight());
                }
            }

            paperScaleOut.setScaling(direction > 0 ? 1 + scale * scalePapers : 1 - scale * scalePapers);
            paperScaleOut.setAlpha(1 - scale);
            paperScaleOut.setRotation(scale, ROTATION_DEGREE, direction);

            paperScaleIn.setScaling(direction > 0 ? 1 - (1 - scale) * scalePapers : 1 + (1 - scale) * scalePapers);
            paperScaleIn.setAlpha(scale);
            paperScaleIn.setRotation(1 - scale, ROTATION_DEGREE, direction);

            paperScaleIn.invalidate();
            paperScaleOut.invalidate();
        }
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        private int direction = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mPrefs.lastSelectedFragment().put(position);

            if (checkDirection) {
                if (positionOffset < thresholdOffset) {
                    direction = +1;
                } else if(1 - position < thresholdOffset) {
                    direction = -1;
                }

                checkDirection = false;
            }

            updatePageScaling(position, direction, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_SETTLING) {
                updateControls();
                updatePaperSize();
            }

            if (!scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING) {
                scrollStarted = true;
                checkDirection = true;
            } else {
                scrollStarted = false;
            }
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

                    updatePaperBleeding(currentBleeding + (int)(difference / stepSize) * unit.getBleedStep());

                    return true;
            }

            return true;
        }
    };

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

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
