package papersize.chreez.com.papersize;

import android.app.Application;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import papersize.chreez.com.papersize.paper.Unit;
import papersize.chreez.com.papersize.sharedpreferences.PaperPreferences_;

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
