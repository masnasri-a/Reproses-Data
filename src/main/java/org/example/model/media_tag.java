package org.example.model;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.service.updateElastic;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

public class media_tag {
    public void updateMediaTag(String arg) throws IOException, ParseException {
        String index = "search-online-news-ima";
        updateElastic elastic = new updateElastic();
        mapper mapper = new mapper();
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.180.221", 5200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                return builder.setConnectTimeout(60000).setSocketTimeout(30000);
            }
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        SearchRequest request;
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //beradasar query match phrase
        searchSourceBuilder.query(matchPhraseQuery("source", arg));
        searchSourceBuilder.size(100);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        System.out.println("UPDATES");
        int page = 1;
        int dataCount = 0;
        int a = 1;
        long start = System.currentTimeMillis();
        while (searchHits != null && searchHits.length > 0) {
            System.out.println(index + " | DataCount => " + dataCount + " | Page => " + page);
            BulkRequest bulkRequest = new BulkRequest();
            for (SearchHit searchHit : searchHits) {
                JSONObject json = (JSONObject) new JSONParser().parse(searchHit.getSourceAsString());
                JSONObject mediatag = new JSONObject();
                String source = searchHit.getSourceAsMap().get("source").toString();
                if (arg.equals(source)) {
                    mapper.reProses(json, mediatag, arg);
                    bulkRequest.add(new UpdateRequest(searchHit.getIndex(), "_doc", searchHit.getId()).doc(mediatag));
//                    elastic.updateData(bulkRequest, searchHit, mediatag);
                }
//                System.out.println(mediatag);
                dataCount++;
            }
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
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

        if (succeeded) {
            long end = System.currentTimeMillis();
            System.out.println("DONE => " + dataCount + " data in " + (end - start) + " ms");

            System.exit(0);
        }
    }
}
