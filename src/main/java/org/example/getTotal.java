package org.example;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

public class getTotal {
    public void Total(List source) throws IOException {
        int total = 0;
        for (int indexs = 0; indexs<source.size(); indexs++) {

            String index = "search-online-news-ima";
//        String index = "ima-online-news";
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.180.221", 5200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            //        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                return builder.setConnectTimeout(60000).setSocketTimeout(30000);
            }
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(matchPhraseQuery("source", source.get(indexs)));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
            System.out.println(indexs+" = "+source.get(indexs) +" = "+searchResponse.getHits().getTotalHits());
            total += Integer.parseInt(String.valueOf(searchResponse.getHits().getTotalHits()));
        }
        System.out.println(total);

    }
}
