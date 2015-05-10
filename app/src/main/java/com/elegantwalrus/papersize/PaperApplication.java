package com.elegantwalrus.papersize;

import android.app.Application;

import com.elegantwalrus.papersize.paper.Unit;
import com.elegantwalrus.papersize.sharedpreferences.PaperPreferences_;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Christopher Gebhardt on 26.04.15.
 */
@EApplication
public class PaperApplication extends Application {

    @Pref
    PaperPreferences_ mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Unit getApplicationUnit() {
        return Unit.values()[mPrefs.unit().get()];
    }
}
