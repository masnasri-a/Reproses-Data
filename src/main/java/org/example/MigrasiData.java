package org.example;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

public class MigrasiData {
    public static void main(String[] args) throws IOException {
        String index = "search-online-news-ima";
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.180.221", 5200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                return builder.setConnectTimeout(60000).setSocketTimeout(30000);
            }
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);

        RestClientBuilder builder1 = RestClient.builder(new HttpHost("localhost", 9200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                return builder.setConnectTimeout(60000).setSocketTimeout(30000);
            }
        });
        RestHighLevelClient client1 = new RestHighLevelClient(builder1);


        SearchRequest request;
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //beradasar query match phrase
        searchSourceBuilder.query(matchPhraseQuery("source", "the international news"));
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
            for (SearchHit searchHit : searchHits) {
                System.out.println(dataCount);
                IndexRequest indexRequest = new IndexRequest();
                indexRequest.index("ima-online-news");
                indexRequest.id(dataCount+"a");
                indexRequest.type("_doc");
                indexRequest.source(searchHit.getSourceAsMap());
                RequestOptions options;
                IndexResponse indexResponse = client1.index(indexRequest, RequestOptions.DEFAULT);
                dataCount++;
            }
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
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

