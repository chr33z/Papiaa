package papersize.chreez.com.papersize;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;

@EActivity(R.layout.activity_about)
public class AboutActivity extends ActionBarActivity {

    @ViewById(R.id.text_icon_play_store)
    TextView mPlayStoreIcon;

    @ViewById(R.id.text_icon_recommend)
    TextView mRecommendIcon;

    @ViewById(R.id.text_icon_acknowledgements)
    TextView mAcknowledgmentsIcon;

    @ViewById(R.id.version)
    TextView mVersion;

    @ViewById(R.id.copyright)
    TextView mCopyright;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @AfterViews
    void onContent() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface awesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        mPlayStoreIcon.setTypeface(awesome);
        mRecommendIcon.setTypeface(awesome);
        mAcknowledgmentsIcon.setTypeface(awesome);

        // Set version name
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            mVersion.setText(String.format(getString(R.string.about_version), version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Set copyright year
        String year = Calendar.getInstance().get(Calendar.YEAR) + "";
        mCopyright.setText(String.format(getString(R.string.about_copyright), year));
    }

    @OptionsItem(android.R.id.home)
    void onHomeButtonPressed() {
        super.onBackPressed();
    }

    @Click(R.id.action_rate_play_store)
    void onButtonPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Click(R.id.action_recommend)
    void onRecommend() {
        String uri = "http://play.google.com/store/apps/details?id=" + getPackageName();
        String message = String.format(getString(R.string.recommend_body), uri);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Click(R.id.action_acknowledgements)
    void onAcknowledgments() {
        AcknowledgmentsActivity_.intent(this).start();
    }
}
