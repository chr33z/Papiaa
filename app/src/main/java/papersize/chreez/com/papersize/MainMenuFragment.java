package papersize.chreez.com.papersize;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;

import papersize.chreez.com.papersize.paper.PaperStandard;

/**
 * Created by chris on 29.04.15.
 */
@EFragment(R.layout.fragment_main_menu)
public class MainMenuFragment extends Fragment {

    private List<PaperStandard> data;

    @ViewById(R.id.container_left)
    LinearLayout mContainer;

    private HashMap<String, TextView> mButtonMap = new HashMap<>();

    private String activeStandard = "";

    @AfterViews
    void onContent() {
        // Add paper standards to container
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Semibold.ttf");
        if(data != null) {
            for (final PaperStandard standard : data) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_group_item, (ViewGroup) getView(), false);
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
            mButtonMap.get(key).animate().alpha(1.0f).start();
        }
    }

    void openFormatsList(PaperStandard standard) {
        // deactivate all buttons
        for (String key : mButtonMap.keySet()) {
            boolean isStandard = key.equals(standard.getName());
            boolean isActive = activeStandard.equals(standard.getName());

            if(isStandard && !isActive) {
                mButtonMap.get(key).animate().alpha(1.0f).start();
            } else if(!isStandard && isActive) {
                mButtonMap.get(key).animate().alpha(0.5f).start();
            } else if(isStandard && isActive) {
                // skip
            } else {
                mButtonMap.get(key).animate().alpha(0.5f).start();
            }
        }

        activeStandard = standard.getName();
        ((MainActivity) getActivity()).openFormatsList(standard);
    }
}
