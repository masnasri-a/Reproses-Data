package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.example.model.media_tag;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Logger logger = (Logger) LogManager.getRootLogger();
        String ars = "china daily asia";
        media_tag media_tag = new media_tag();
        // source media_tag yang mau diubah
        List<String> arg = new ArrayList<String>(Arrays.asList("indeks news kalteng", "kabar banua", "mitra sulawesi", "kaji news", "periksa21", "cyber news nasional", "sulbarupdate", "china daily asia"));
        if (arg.contains(ars)) {
            System.out.println("Data Is Correct"+ars);
            media_tag.updateMediaTag(ars);

        } else {
            logger.error("ARG INPUT FAILURE!!");
            System.exit(0);
        }


//        System.exit(0);
    }
}
