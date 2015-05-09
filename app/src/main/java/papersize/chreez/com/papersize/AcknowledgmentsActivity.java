package papersize.chreez.com.papersize;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_acknowledgments)
public class AcknowledgmentsActivity extends ActionBarActivity {

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.web)
    WebView mWebview;

    @AfterViews
    void onContent() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebview.loadUrl("file:///android_asset/acknowledgments.html");
    }
}
