package kaleidoscope.loading;

import com.fs.starfarer.api.Global;
import kaleidoscope.ids.Ids;
import kaleidoscope.plugins.ModPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Importer {

    public static List<ImageDataEntry> loadImageData(){
        List<ImageDataEntry> dataMap = new ArrayList<>();

        try {
            JSONArray config = Global.getSettings().getMergedSpreadsheetDataForMod("id", Ids.CSV_PATH, Ids.MOD_ID);
            for (int i = 0; i < config.length(); i++) {

                JSONObject row = config.getJSONObject(i);
                int id = row.getInt("id");
                String imageName = row.getString("image_name").replaceAll("\\s", "");
                String glowName = row.getString("glow_name").replaceAll("\\s", "");
                String cloudName = row.getString("cloud_name").replaceAll("\\s", "");
                String planetType = row.getString("planet_type").replaceAll("\\s", "");
                String typeName = row.getString("type_name");
                String descId = row.getString("desc").replaceAll("\\s", "");
                boolean reverseGlow = !row.getString("reverse_glow").isEmpty() && row.getBoolean("reverse_glow");

                ModPlugin.log("reverse glow " + glowName + " is " + reverseGlow);

                List<String> conditionsToAdd = new ArrayList<>();

                for (String s : row.getString("force_condition").split("\\s+")){
                    s = s.replaceAll("\\s", "");
                    if (s.length() < 3) continue;
                    conditionsToAdd.add(s);
                }
                ImageDataEntry imageEntry = new ImageDataEntry(
                        id,
                        conditionsToAdd,
                        imageName,
                        glowName,
                        cloudName,
                        reverseGlow,
                        planetType,
                        typeName,
                        descId);

                dataMap.add(imageEntry);
            }
        } catch (IOException | JSONException ex) {
            ModPlugin.log.error("Could not find Kaleidoscope planet_texture_data, or something is wrong with the data format.", ex);
        }

       return dataMap;
    }


    private static boolean isNumber(String s) {
        if (s.isEmpty()) {
            return false;
        }
        char ch = s.charAt(0);
        return ((ch >= '0') && (ch <= '9'));
    }
}
