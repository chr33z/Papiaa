package papersize.chreez.com.papersize;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import papersize.chreez.com.papersize.data.FormatLoader;
import papersize.chreez.com.papersize.paper.Orientation;
import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperStandard;
import papersize.chreez.com.papersize.paper.Unit;
import papersize.chreez.com.papersize.sharedpreferences.PaperPreferences_;

@EActivity(R.layout.activity_paper_viewer)
@OptionsMenu(R.menu.menu_paper_viewer)
public class PaperViewerActivity extends ActionBarActivity {

    private static final int MAX_BLEEDING = 20;

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

    @ViewById(R.id.icon_toogle_orientation)
    ImageView mToogleOrientation;

    private float bleedingTouchX;
    private double currentBleeding;

    @ViewById(R.id.pager)
    ViewPager mPager;

    private ScreenSlidePagerAdapter mPagerAdapter;

    private int screenWidth;

    private PaperStandard mStandard;

    DecimalFormat doubleFormat = new DecimalFormat("#.#");

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OptionsItem(R.id.action_share_paper)
    void onSharePaper() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        View view = fragment.getView();

        Bitmap returnedBitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        bgDrawable.draw(canvas);
        view.draw(canvas);
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

    private void updatePaperBleeding(double bleed) {
        if(bleed >= 0 && bleed <= MAX_BLEEDING) {
            if(bleed == 0) {
                mLabelBleeding.setText(
                        getText(R.string.label_add_bleeding));
            } else {
                mLabelBleeding.setText(
                        getText(R.string.label_bleeding) + " " + bleed + " " + Unit.MILLIMETER.getName());
            }

            if(bleed <= 0) {
                mDecreaseBleed.setEnabled(false);
                mDecreaseBleed.animate().alpha(0.5f).start();
            } else if(bleed >= MAX_BLEEDING) {
                mIncreaseBleed.setEnabled(false);
                mIncreaseBleed.animate().alpha(0.5f).start();
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

    @Click(R.id.icon_toogle_orientation)
    void togglePaperOrientation() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        fragment.togglePaperOrientation();

        updateOrientationIcon(fragment.getPaper());
        updatePaperSize();
    }

    private void updateControls() {
        PaperCanvasFragment fragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
        updatePaperBleeding(fragment.getPaper().getBleed());
        updateOrientationIcon(fragment.getPaper());

    }

    private void updateOrientationIcon(Paper paper) {
        if (paper.getOrientation() == Orientation.PORTRAIT) {
            mToogleOrientation.setImageResource(R.drawable.icon_orientation_portrait);
        } else {
            mToogleOrientation.setImageResource(R.drawable.icon_orientation_landscape);
        }
    }

    private void updatePaperSize() {
        Paper paper = mPagerAdapter.getFragment(mPager.getCurrentItem()).getPaper();
        String width = doubleFormat.format(paper.getWidth() + paper.getBleed() * 2);
        String height = doubleFormat.format(paper.getHeight() + paper.getBleed() * 2);
        mPaperSize.setText(String.format(Locale.getDefault(), getString(R.string.format_paper_size), width, height));
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
                    int stepSize = maxValue / MAX_BLEEDING;

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
