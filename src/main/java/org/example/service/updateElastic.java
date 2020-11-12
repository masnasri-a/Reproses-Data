package org.example.service;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;

public class updateElastic {
    public BulkRequest updateData(BulkRequest client, SearchHit searchHit, JSONObject media_tags){
        Boolean status  = false;
        while (!status){
            try {
                System.out.println(media_tags);
                client.add(new UpdateRequest(searchHit.getIndex(), "_doc", searchHit.getId()).doc(media_tags));
//                UpdateRequest request = new UpdateRequest(searchHit.getIndex(), "_doc", searchHit.getId());
//                request.doc(media_tags, XContentType.JSON).docAsUpsert(true).retryOnConflict(30);
//                UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
                status = true;
                System.out.println(searchHit.getId()+" Inserted Into "+searchHit.getIndex());
                System.out.println("============================");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return client;
    }
}
