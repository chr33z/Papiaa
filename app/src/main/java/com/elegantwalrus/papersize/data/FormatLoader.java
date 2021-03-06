package com.elegantwalrus.papersize.data;

import android.content.Context;

import com.elegantwalrus.papersize.PaperApplication;
import com.elegantwalrus.papersize.R;
import com.elegantwalrus.papersize.paper.Paper;
import com.elegantwalrus.papersize.paper.PaperStandard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class FormatLoader {

    /**
     * Load paper formats from a json file. This method loads a german
     * paper format file when german locale is set, otherwise it loads
     * an international format file.
     * This method also checks the database for favorites, and default orientation.
     *
     * @param context
     * @return
     */
    public static List<PaperStandard> readPaperFile(Context context) {
        // Get favorites from database
        FavoriteStore fs = new FavoriteStore(context);
        fs.open();
        List<String> favorites = fs.getFavorites();
        fs.close();

        int rawResource;
        Locale locale = Locale.getDefault();
        if(locale.equals(Locale.GERMANY) || locale.equals(Locale.GERMAN)) {
            rawResource = R.raw.paper_formats_de;
        } else {
            rawResource = R.raw.paper_formats_int;
        }

        List<PaperStandard> standards = new ArrayList<>();

        InputStream inputStream = context.getResources().openRawResource(rawResource);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONObject json = new JSONObject(byteArrayOutputStream.toString());
            JSONArray standardsArray = json.getJSONObject("paper").getJSONArray("standards");

            for (int i = 0; i < standardsArray.length(); i++) {
                JSONObject standardObj =  standardsArray.getJSONObject(i);

                String standardName = standardObj.getString("name");
                String standardDescription = standardObj.getString("description");
                PaperStandard paperStandard = new PaperStandard(standardName, standardDescription);

                JSONArray formatsArray = standardObj.getJSONArray("formats");
                for (int k = 0; k < formatsArray.length(); k++) {
                    JSONObject formatsObj =  formatsArray.getJSONObject(k);

                    String formatName = formatsObj.getString("name");
                    String formatId = formatsObj.getString("id");
                    String formatDescription = formatsObj.getString("description");
                    int width = formatsObj.getInt("width");
                    int height = formatsObj.getInt("height");

                    Paper paper = new Paper(formatName, formatDescription, formatId, width, height);
                    paper.setFavorite(isInFavorites(favorites, formatId));
                    paper.setOrientation(((PaperApplication)context.getApplicationContext()).
                            getApplicationOrientation());

                    paperStandard.addPaper(paper);
                }

                standards.add(paperStandard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return standards;
    }

    private static boolean isInFavorites(List<String> favorites, String id) {
        for (String favorite : favorites) {
            if(favorite.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
