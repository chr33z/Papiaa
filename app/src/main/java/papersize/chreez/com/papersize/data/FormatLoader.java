package papersize.chreez.com.papersize.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import papersize.chreez.com.papersize.R;
import papersize.chreez.com.papersize.paper.Paper;
import papersize.chreez.com.papersize.paper.PaperSeries;
import papersize.chreez.com.papersize.paper.PaperStandard;

/**
 * Created by Christopher Gebhardt on 23.04.15.
 */
public class FormatLoader {

    public static List<PaperStandard> readPaperFile(Context context) {
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

                JSONArray seriesArray = standardObj.getJSONArray("series");
                for (int j = 0; j < seriesArray.length(); j++) {
                    JSONObject seriesObj =  seriesArray.getJSONObject(j);

                    String seriesName = seriesObj.getString("name");
                    String seriesDescription = seriesObj.getString("description");
                    PaperSeries paperSeries = new PaperSeries(seriesName, seriesDescription);

                    JSONArray formatsArray = seriesObj.getJSONArray("formats");
                    for (int k = 0; k < formatsArray.length(); k++) {
                        JSONObject formatsObj =  formatsArray.getJSONObject(k);

                        String formatName = formatsObj.getString("name");
                        String formatDescription = formatsObj.getString("description");
                        int width = formatsObj.getInt("width");
                        int height = formatsObj.getInt("height");

                        Paper paper = new Paper(formatName, formatDescription, width, height);
                        paperSeries.addFormat(paper);
                    }

                    paperStandard.addSeries(paperSeries);
                }

                standards.add(paperStandard);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return standards;
    }
}
