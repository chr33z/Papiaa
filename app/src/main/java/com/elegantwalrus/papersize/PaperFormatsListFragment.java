package com.elegantwalrus.papersize;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.elegantwalrus.papersize.adapter.PaperListAdapter;
import com.elegantwalrus.papersize.data.FavoriteStore;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.PaperStandard;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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
    }

    private List<String> getFavorites() {
        List<String> favorites = new ArrayList<>();
        FavoriteStore fs = new FavoriteStore(getActivity());
        fs.open();
        favorites = fs.getFavorites();
        fs.close();
        return favorites;
    }
}
