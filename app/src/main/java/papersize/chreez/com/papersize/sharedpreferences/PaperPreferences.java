package papersize.chreez.com.papersize.sharedpreferences;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Christopher Gebhardt on 26.04.15.
 */
@SharedPref
public interface PaperPreferences {

    @DefaultInt(0)
    int unit();
}
