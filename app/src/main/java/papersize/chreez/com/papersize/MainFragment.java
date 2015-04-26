package papersize.chreez.com.papersize;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperSeries;
import papersize.chreez.com.papersize.paper.PaperStandard;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @ViewById(R.id.list)
    ListView mListView;

    PaperMenuAdapter mAdapter;

    List<PaperStandard> standards;

    @AfterViews
    void onContent() {
        List<PaperMenuItem> menuItems = new ArrayList<>();

        if(standards != null) {
            StringBuilder builder = new StringBuilder();
            for (PaperStandard standard : standards) {
                for (PaperSeries series : standard.getSeries()) {
                    for (Paper paper : series.getFormats()) {
                        builder.append(standard.getName() + " | ");
                        builder.append(series.getName() + " | ");
                        builder.append(paper.getName());

                        PaperMenuItem item = new PaperMenuItem(
                                builder.toString(), paper, 0, 0);
                        menuItems.add(item);

                        builder.delete(0, builder.length());
                    }
                }
            }
        }

        mAdapter = new PaperMenuAdapter(getActivity(), menuItems);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onItemClickListener);
    }

    public void setData(List<PaperStandard> standards) {
        this.standards = standards;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PaperMenuItem item = mAdapter.getItem(position);

            ((MainActivity) getActivity()).openPaperViewer(item.paper);
        }
    };
}
