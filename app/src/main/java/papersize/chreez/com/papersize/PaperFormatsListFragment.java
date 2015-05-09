package papersize.chreez.com.papersize;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import papersize.chreez.com.papersize.adapter.PaperListAdapter;
import papersize.chreez.com.papersize.data.FavoriteStore;
import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperStandard;

/**
 * Created by chris on 29.04.15.
 */
@EFragment(R.layout.fragment_list)
public class PaperFormatsListFragment extends Fragment {

    private PaperStandard mStandard;

    @ViewById(R.id.list)
    ListView mList;

    private PaperListAdapter mAdapter;

    @AfterViews
    void onContent() {
        if(mStandard != null) {
            mAdapter = new PaperListAdapter(getActivity(), mStandard.getFormats());
            mList.setAdapter(mAdapter);

            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Paper paper = mAdapter.getItem(position);
                    openPaperViewer(mStandard, paper);
                }
            });

            mList.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.margin_medium));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateFavorites();
    }

    public void setData(PaperStandard data) {
        this.mStandard = data;
    }

    private void openPaperViewer(PaperStandard standard, Paper paper) {
        ((MainActivity) getActivity()).openPaperViewer(standard, paper);
    }

    private void updateFavorites() {
        if(mAdapter != null) {
            mAdapter.setFavorites(getFavorites());
            mAdapter.notifyDataSetChanged();
        }
    };

    private List<String> getFavorites() {
        List<String> favorites = new ArrayList<>();
        FavoriteStore fs = new FavoriteStore(getActivity());
        try {
            fs.open();
            favorites = fs.getFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fs.close();
        }
        return favorites;
    }
}
