package com.study.milvus.service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.study.milvus.dto.TestRecord;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MilvusEmbeddingService {
    private static final Logger log = LoggerFactory.getLogger(MilvusEmbeddingService.class);

    private static final String MILVUS_HOST = "http://localhost:19530";
    private static final String COLLECTION_NAME = "customized_setup_2";
    //向量维度定义1536，跟阿里巴巴embedding向量服务返回的维度保持一致
    private static final int VECTOR_DIM = 1536;

    @Autowired
    private EmbeddingModel embeddingModel;

    private MilvusClientV2 client;

    @PostConstruct
    public void init() {
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(MILVUS_HOST)
                .build();
        client = new MilvusClientV2(connectConfig);
    }

    /**
     * 创建一个Collection
     */
    public void createCollection() {
        CreateCollectionReq.CollectionSchema schema = client.createSchema();

        schema.addField(AddFieldReq.builder()
                .fieldName("id")
                .dataType(DataType.VarChar)
                .isPrimaryKey(true)
                .autoID(false)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName("title")
                .dataType(DataType.VarChar)
                .maxLength(10000)
                .build());

        schema.addField(AddFieldReq.builder()
                .fieldName("title_vector")
                .dataType(DataType.FloatVector)
                .dimension(VECTOR_DIM)
                .build());

        IndexParam indexParam = IndexParam.builder()
                .fieldName("title_vector")
                .metricType(IndexParam.MetricType.COSINE)
                .build();

        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                .collectionName(COLLECTION_NAME)
                .collectionSchema(schema)
                .indexParams(Collections.singletonList(indexParam))
                .build();

        client.createCollection(createCollectionReq);
    }

    /**
     * 往collection中插入一条数据
     */
    public void insertRecord(TestRecord record) {
        JsonObject vector = new JsonObject();
        vector.addProperty("id", record.getId());
        vector.addProperty("title", record.getTitle());
        List<Float> vectorList = new ArrayList<>();
        Gson gson = new Gson();
        //调用阿里向量模型服务，返回1536维向量float
        float[] floatArray = embeddingModel.embed(record.getTitle());
        for (float f : floatArray) {
            vectorList.add(f);
        }

        vector.add("title_vector", gson.toJsonTree(vectorList));

        InsertReq insertReq = InsertReq.builder()
                .collectionName(COLLECTION_NAME)
                .data(Collections.singletonList(vector))
                .build();
        InsertResp resp = client.insert(insertReq);
    }


    /**
     * 按照向量检索，找到相似度最近的topK
     */
    public List<List<SearchResp.SearchResult>>  queryVector(String queryText) {
        //调用阿里向量模型服务，对查询条件进行向量化
        float[] floatArray = embeddingModel.embed(queryText);

        SearchResp searchR = client.search(SearchReq.builder()
                .collectionName(COLLECTION_NAME)
                .data(Collections.singletonList(new FloatVec(floatArray)))
                .topK(3)
                .outputFields(Collections.singletonList("*"))
                .build());
        List<List<SearchResp.SearchResult>> searchResults = searchR.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            for (SearchResp.SearchResult result : results) {
                log.info("ID="+(String)result.getId() + ",Score="+result.getScore() + ",Result="+result.getEntity().toString());
            }
        }
        return searchResults;
    }

}
