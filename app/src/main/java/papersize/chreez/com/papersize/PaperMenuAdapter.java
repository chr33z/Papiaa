package papersize.chreez.com.papersize;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class PaperMenuAdapter extends BaseAdapter {

    private List<PaperMenuItem> items = new ArrayList<>();

    private Context context;

    public PaperMenuAdapter(Context context, List<PaperMenuItem> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PaperMenuItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        }

        PaperMenuItem item = getItem(position);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        textView.setText(item.content);
        textView.setBackgroundColor(item.color);

        return convertView;
    }
}
