package papersize.chreez.com.papersize.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import papersize.chreez.com.papersize.R;
import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperStandard;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class FormatLoader {

    public static List<PaperStandard> readPaperFile(Context context) {
        // Get favorites from database
        FavoriteStore fs = new FavoriteStore(context);
        List<String> favorites = new ArrayList<>();
        try {
            fs.open();
            favorites = fs.getFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fs.close();
        }

        List<PaperStandard> standards = new ArrayList<>();

        InputStream inputStream = context.getResources().openRawResource(R.raw.paper_formats);
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
