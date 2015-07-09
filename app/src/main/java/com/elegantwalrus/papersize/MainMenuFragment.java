package com.elegantwalrus.papersize;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elegantwalrus.papersize.paper.PaperStandard;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;

/**
 * Created by chris on 29.04.15.
 */
@EFragment(R.layout.fragment_main_menu)
public class MainMenuFragment extends Fragment {

    private List<PaperStandard> data;

    @ViewById(R.id.container_left)
    LinearLayout mContainer;

    private final HashMap<String, TextView> mButtonMap = new HashMap<>();

    private String activeStandard = "";

    @AfterViews
    void onContent() {
        // Add paper standards to container
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Semibold.ttf");
        if(data != null) {
            for (final PaperStandard standard : data) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_favorite, (ViewGroup) getView(), false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFormatsList(standard);
                    }
                });

                TextView textView = (TextView) view.findViewById(R.id.text);
                textView.setTypeface(font);
                textView.setText(standard.getName());

                mButtonMap.put(standard.getName(), textView);
                mContainer.addView(view);
            }
        }
    }

    public void setData(List<PaperStandard> data) {
        this.data = data;
    }

    public void resetButtons() {
        activeStandard = "";

        for (String key : mButtonMap.keySet()) {
            animateButtonNormal(mButtonMap.get(key));
        }
    }

    private void openFormatsList(PaperStandard standard) {
        // deactivate all buttons
        for (String key : mButtonMap.keySet()) {
            boolean isStandard = key.equals(standard.getName());
            boolean isActive = activeStandard.equals(standard.getName());

            if(isStandard && !isActive) {
                animateButtonActive(mButtonMap.get(key));
            } else if(!isStandard && isActive) {
                animateButtonInactive(mButtonMap.get(key));
            } else if(isStandard && isActive) {
                // skip
            } else {
                animateButtonInactive(mButtonMap.get(key));
            }
        }

        activeStandard = standard.getName();
        ((MainActivity) getActivity()).openFormatsList(standard);
    }

    private void animateButtonActive(View view) {
        view.animate().alpha(1.0f).start();
        TextView textView = (TextView)view.findViewById(R.id.text);
        if(textView != null) {
            animateTextColor(textView, getResources().getColor(R.color.primary));
        }
    }

    private void animateButtonInactive(View view) {
        view.animate().alpha(0.5f).start();
        TextView textView = (TextView)view.findViewById(R.id.text);
        if(textView != null) {
            animateTextColor(textView, getResources().getColor(R.color.material_blue_grey_800));
        }
    }

    private void animateButtonNormal(View view) {
        view.animate().alpha(1.0f).start();
        TextView textView = (TextView)view.findViewById(R.id.text);
        if(textView != null) {
            animateTextColor(textView, getResources().getColor(R.color.material_blue_grey_800));
        }
    }

    private void animateTextColor(final TextView textView, int color) {
        final float[] from = new float[3];
        final float[] to = new float[3];
        final float[] hsv  = new float[3];

        Color.colorToHSV(textView.getCurrentTextColor(), from);
        Color.colorToHSV(color, to);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                textView.setTextColor(Color.HSVToColor(hsv));
            }
        });
        anim.start();
    }
}
