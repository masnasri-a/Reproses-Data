package org.example;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

public class getelsaticdata {
    public void get_ann(List source, List media) throws IOException {
        String index = "search-online-news-ima";
        List<List> new_media_tag = new ArrayList<>();
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.180.221", 5200)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                return builder.setConnectTimeout(60000).setSocketTimeout(30000);
            }
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            SearchRequest searchRequest = new SearchRequest(index);
        for (int a = 0; a<media.size(); a++) {
            //beradasar query match phrase
            searchSourceBuilder.query(matchPhraseQuery("source", source.get(a)));
        }
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        int w = 0;
        for (SearchHit hasil : searchHits) {
            System.out.println(source.get(w)+" = "+hasil.getSourceAsMap().get("ann_content_type"));
            w++;
        }
        System.out.println(w);
    }
}
