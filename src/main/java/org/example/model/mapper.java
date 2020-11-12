package org.example.model;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mapper {
    int a = 0;
    public void reProses(JSONObject json, JSONObject media_tags, String arg) {
        List<String> change = new ArrayList<>();
        switch (arg) {
            case "indeks news kalteng":
                change = new ArrayList<>(Arrays.asList("indeksnewskalteng", "nationalwebsite", "indonesia", "general", "kalimantantengah", "ebdeskind"));
                break;
            case "kabar banua":
                change = new ArrayList<>(Arrays.asList("kabarbanua", "national", "website", "indonesia", "general", "tanahbumbu", "kalimantanselatan", "ebdeskind"));
                break;
            case "mitra sulawesi":
                change = new ArrayList<>(Arrays.asList("mitrasulawesi", "national", "website", "indonesia", "general", "gowa", "sulawesiselatan", "ebdeskind"));

                break;
            case "kaji news":
                change = new ArrayList<>(Arrays.asList("kajinews", "national", "website", "indonesia", "general", "makassar", "sulawesiselatan", "ebdeskind"));

                break;
            case "periksa21":
                change = new ArrayList<>(Arrays.asList("periksa21", "national", "website", "indonesia", "general", "ebdeskind"));

                break;
            case "cyber news nasional":
                change = new ArrayList<>(Arrays.asList("cybernewsnasional", "national", "website", "indonesia", "general", "tangerang", "banten", "ebdeskind"));

                break;
            case "sulbarupdate":
                change = new ArrayList<>(Arrays.asList("sulbarupdate", "national", "website", "indonesia", "general", "sulawesibarat", "ebdeskind"));

                break;
            case "china daily asia":
                change = new ArrayList<>(Arrays.asList("international", "hongkong", "chinadailyasia", "general", "ebdeskind"));
                break;
        }
        System.out.println(a+" = "+arg+" = "+change);
        a++;
        media_tags.put("media_tags", change);
        change = new ArrayList<>();

    }
}
