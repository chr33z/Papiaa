package com.elegantwalrus.papersize;

import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

@EActivity(R.layout.activity_paper_settings)
public class PaperSettingsActivity extends ActionBarActivity {

    @AfterViews
    void onContent() {
        getSupportActionBar().setTitle(R.string.title_activity_paper_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new PaperSettingsFragment_())
                .commit();
    }

    @OptionsItem(android.R.id.home)
    void onHomeButton() {
        onBackPressed();
    }
}
