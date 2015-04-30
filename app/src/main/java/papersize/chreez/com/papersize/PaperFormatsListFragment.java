package papersize.chreez.com.papersize;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

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

    private PaperFormatsAdapter mAdapter;

    @AfterViews
    void onContent() {
        if(mStandard != null) {
            mAdapter = new PaperFormatsAdapter(getActivity(), mStandard);
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

    public void setData(PaperStandard data) {
        this.mStandard = data;
    }

    private void openPaperViewer(PaperStandard standard, Paper paper) {
        ((MainActivity) getActivity()).openPaperViewer(standard, paper);
    }

    private class PaperFormatsAdapter extends BaseAdapter {

        PaperStandard data;

        Context context;

        Typeface openSansSemiBold;

        public PaperFormatsAdapter(Context context, PaperStandard data) {
            this.data = data;
            this.context = context;

            openSansSemiBold = Typeface.createFromAsset(context.getAssets(), "OpenSans-Semibold.ttf");
        }

        @Override
        public int getCount() {
            return data.getFormats().size();
        }

        @Override
        public Paper getItem(int position) {
            return data.getFormats().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_group_item, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setTypeface(openSansSemiBold);
            textView.setText(getItem(position).getName());

            return convertView;
        }
    }
}
