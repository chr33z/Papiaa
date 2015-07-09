package com.elegantwalrus.papersize.sharedpreferences;

import com.elegantwalrus.papersize.R;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultRes;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Christopher Gebhardt on 26.04.15.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface PaperPreferences {

    @DefaultRes(R.integer.default_unit)
    int unit();

    @DefaultRes(R.integer.default_unit)
    int orientation();

    @DefaultInt(0)
    int lastSelectedFragment();
}
