package org.example;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

public class getTotalHits {
    public void getTotalHit(int number, List<String> source, List<List<String>> media) throws IOException {
        String index = "search-online-news-ima";
//        String index = "ima-online-news";
        try {

            RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.180.221", 5200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                //        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                    return builder.setConnectTimeout(60000).setSocketTimeout(30000).setConnectionRequestTimeout(20000);
                }
            });
            RestHighLevelClient client = new RestHighLevelClient(builder);


            SearchRequest request;
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.scroll(scroll);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            //beradasar query match phrase
            searchSourceBuilder.query(matchPhraseQuery("source", source.get(number)));
            searchSourceBuilder.size(100);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            System.out.println("UPDATES");
            int page = 1;
            int dataCount = 1;
            int a = 1;
            List<String> medtag = new ArrayList<>();

            JSONObject js = new JSONObject();

            while (searchHits != null && searchHits.length > 0) {
                BulkRequest bulkRequest = new BulkRequest();
                System.out.println(index + " | DataCount => " + dataCount + " | Page => " + page);
                for (SearchHit searchHit : searchHits) {
                    try {
                        for (int w = 0; w < media.get(number).size(); w++) {
                            medtag.add(media.get(number).get(w));
                        }
                        String id = searchHit.getId();
                        if (!searchHit.getSourceAsMap().get("ann_content_type").equals("")) {
                            medtag.add(searchHit.getSourceAsMap().get("ann_content_type").toString());
                        }
                        js.put("media_tags", medtag.stream().distinct().collect(Collectors.toList()));
//                System.out.println(js);
                        bulkRequest.add(new UpdateRequest(searchHit.getIndex(), "_doc", searchHit.getId()).doc(js.toString(), XContentType.JSON).docAsUpsert(true).retryOnConflict(30));
//                    System.out.println(dataCount + " = " + searchHit.getIndex() + " = " + source.get(number) + " = " + searchHit.getId() + " = " + js);
//                    System.out.println(searchHit.getSourceAsMap().get("ann_content_type"));
                        js = new JSONObject();
                        medtag.clear();
                        dataCount++;
                    } catch (NullPointerException e) {
//                    System.out.println("Not Found "+e.getMessage());
                    }
                }
                long start = System.currentTimeMillis();
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                long end = System.currentTimeMillis();
                System.out.println("Bulk Upload to elastic = " + source.get(number) + " = " + (end - start) + " ms");
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                searchHits = searchResponse.getHits().getHits();
                page++;
            }
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
            client.close();
        }catch (SocketTimeoutException w){
            System.out.println(w.getMessage());
            System.out.println(w.getCause());
        }catch (ActionRequestValidationException s){
            System.out.println(s.getMessage());
        }
    }
}
