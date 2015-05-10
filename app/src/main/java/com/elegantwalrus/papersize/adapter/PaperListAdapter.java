package com.elegantwalrus.papersize.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elegantwalrus.papersize.R;
import com.elegantwalrus.papersize.paper.Paper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaperListAdapter extends BaseAdapter {

        List<Paper> data = new ArrayList<>();

        List<Paper> sortedData = new ArrayList<>();

        Context context;

        Typeface openSansSemiBold;

        public PaperListAdapter(Context context, List<Paper> data) {
            this.data = data;
            this.context = context;

            sortedData.addAll(this.data);

            openSansSemiBold = Typeface.createFromAsset(context.getAssets(), "OpenSans-Semibold.ttf");
        }

        public void setFavorites(List<String> favorites) {
            sortedData.clear();
            sortedData.addAll(data);

            List<Paper> favoritePapers = new ArrayList<>();

            Iterator<Paper> iterator = sortedData.iterator();
            while(iterator.hasNext()) {
                Paper paper = iterator.next();

                if(isInFavorites(favorites, paper)) {
                    paper.setFavorite(true);
                    favoritePapers.add(paper);
                    iterator.remove();
                } else {
                    paper.setFavorite(false);
                }
            }

            List<Paper> newData = favoritePapers;
            favoritePapers.addAll(sortedData);
            sortedData = favoritePapers;
        }

        private boolean isInFavorites(List<String> favorites, Paper paper) {
            for (String favorite : favorites) {
                if(favorite.equals(paper.getId())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getCount() {
            return sortedData.size();
        }

        @Override
        public Paper getItem(int position) {
            return sortedData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_favorite, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setTypeface(openSansSemiBold);
            textView.setText(getItem(position).getName());

            ImageView indicator = (ImageView) convertView.findViewById(R.id.favorite_indicator);
            if(getItem(position).isFavorite()) {
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }