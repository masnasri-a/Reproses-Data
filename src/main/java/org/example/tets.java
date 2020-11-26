package org.example;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tets {
    public static void main(String[] args) throws ArrayIndexOutOfBoundsException {
        getelsaticdata elastic = new getelsaticdata();
        getTotalHits getTotalHits = new getTotalHits();
        File path = new File(args[0]);
        try {
            FileReader fileReader = new FileReader(path);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] nextRecord;
            List<String> source = new ArrayList<>();
            List<String> media_tags = new ArrayList<>();
            List<List<String>> media_tag = new ArrayList<>();
            while ((nextRecord= csvReader.readNext())!= null ){
                if (!nextRecord[0].equals("\uFEFFsource")){
                    source.add(nextRecord[0]);
                    media_tags.add(nextRecord[1]);
                }
            }
//            System.out.println(source);
//            System.out.println(media_tags);
            for (int a = 0; a< source.size(); a++){
                String w = media_tags.get(a).toString().replace("|",",");
                String s[] = w.split(",");
                List<String> list = new ArrayList<>();
                list= Arrays.asList(s);
                media_tag.add(list);
                //                System.out.println(source.get(a)+" = "+ );
            }
            for (int a = 0; a< source.size(); a++){
//            System.out.println(source.get(a)+" = "+media_tag.get(a));
            }
//            elastic.get_ann(source,media_tag);
            for (int x = 0; x < source.size(); x++) {
                getTotalHits.getTotalHit(x, source, media_tag);
            }
//                getTotal getTotal = new getTotal();
//            getTotal.Total(source);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
