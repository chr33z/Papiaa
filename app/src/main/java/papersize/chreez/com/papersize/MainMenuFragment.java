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

                mContainer.addView(view);
            }
        }

        // TODO Add Favorites
        View spacer = LayoutInflater.from(getActivity()).inflate(R.layout.list_space_item, (ViewGroup) getView(), false);
        mContainer.addView(spacer);

        View favoriteView = LayoutInflater.from(getActivity()).inflate(R.layout.list_group_item, (ViewGroup) getView(), false);
        favoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView favoriteText = (TextView) favoriteView.findViewById(R.id.text);
        favoriteText.setTypeface(font);
        favoriteText.setTextColor(getActivity().getResources().getColor(R.color.material_deep_orange));
        favoriteText.setText("Favorites");
        mContainer.addView(favoriteView);

        // TODO Add Own paper
    }

    public void setData(List<PaperStandard> data) {
        this.data = data;
    }

    void openFormatsList(PaperStandard standard) {
        ((MainActivity) getActivity()).openFormatsList(standard);
    }

    void openFavoritesList() {

    }
}
