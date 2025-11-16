package com.study.milvus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MilvusApplication {
    // 本地 Docker 部署默认配置（无需修改）
    private static final String MILVUS_HOST = "http://localhost:19530";
    private static final int VECTOR_DIMENSION = 5;
    private static final String COLLECTION_NAME = "customized_setup_1";;


    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(MilvusApplication.class, args);
//       // 1. 初始化 Milvus 客户端
//        ConnectConfig connectConfig = ConnectConfig.builder()
//                .uri(MILVUS_HOST)
//                .build();
//        MilvusClientV2 milvusClient = new MilvusClientV2(connectConfig);
//        System.out.println("✅ Milvus客户端初始化成功");
//
//        milvusClient.dropCollection(DropCollectionReq.builder()
//                .collectionName(COLLECTION_NAME)
//                .build());
//
//        CreateCollectionReq.CollectionSchema schema = MilvusClientV2.CreateSchema();
//        //定义集合字段
//        schema.addField(AddFieldReq.builder()
//                .fieldName("id")
//                .dataType(DataType.Int64)
//                .isPrimaryKey(true)
//                .autoID(false)
//                .build());
//
//        schema.addField(AddFieldReq.builder()
//                .fieldName("vector")
//                .dataType(DataType.FloatVector)
//                .dimension(VECTOR_DIMENSION)
//                .build());
//        //定义索引参数
//        IndexParam indexParamForIdField = IndexParam.builder()
//                .fieldName("id")
//                .indexType(IndexParam.IndexType.AUTOINDEX)
//                .build();
//
//        IndexParam indexParamForVectorField = IndexParam.builder()
//                .fieldName("vector")
//                .indexType(IndexParam.IndexType.AUTOINDEX)
//                .metricType(IndexParam.MetricType.COSINE)
//                .build();
//
//        List<IndexParam> indexParams = new ArrayList<>();
//        indexParams.add(indexParamForIdField);
//        indexParams.add(indexParamForVectorField);
//        //创建集合
//        CreateCollectionReq customizedSetupReq1 = CreateCollectionReq.builder()
//                .collectionName(COLLECTION_NAME)
//                .collectionSchema(schema)
//                .indexParams(indexParams)
//                .build();
//
//        milvusClient.createCollection(customizedSetupReq1);
//
//        LoadCollectionReq loadReq = LoadCollectionReq.builder()
//                .collectionName(COLLECTION_NAME)
//                .build();
//
//        milvusClient.loadCollection(loadReq);
//        System.out.println("✅ 集合加载成功");
//
//        ListCollectionsResp resp = milvusClient.listCollections();
//        System.out.println(resp.getCollectionNames());
//
//        //集合信息
//        DescribeCollectionReq request = DescribeCollectionReq.builder()
//                .collectionName(COLLECTION_NAME)
//                .build();
//        DescribeCollectionResp describeCollection = milvusClient.describeCollection(request);
//        System.out.println(describeCollection);
//
//        Gson gson = new Gson();
//        List<JsonObject> data = Arrays.asList(
//                gson.fromJson("{\"id\": 0, \"vector\": [0.3580376395471989, -0.6023495712049978, 0.18414012509913835, -0.26286205330961354, 0.9029438446296592]}", JsonObject.class),
//                gson.fromJson("{\"id\": 1, \"vector\": [0.19886812562848388, 0.06023560599112088, 0.6976963061752597, 0.2614474506242501, 0.838729485096104]}", JsonObject.class),
//                gson.fromJson("{\"id\": 2, \"vector\": [0.43742130801983836, -0.5597502546264526, 0.6457887650909682, 0.7894058910881185, 0.20785793220625592]}", JsonObject.class),
//                gson.fromJson("{\"id\": 3, \"vector\": [0.3172005263489739, 0.9719044792798428, -0.36981146090600725, -0.4860894583077995, 0.95791889146345]}", JsonObject.class),
//                gson.fromJson("{\"id\": 4, \"vector\": [0.4452349528804562, -0.8757026943054742, 0.8220779437047674, 0.46406290649483184, 0.30337481143159106]}", JsonObject.class),
//                gson.fromJson("{\"id\": 5, \"vector\": [0.985825131989184, -0.8144651566660419, 0.6299267002202009, 0.1206906911183383, -0.1446277761879955]}", JsonObject.class),
//                gson.fromJson("{\"id\": 6, \"vector\": [0.8371977790571115, -0.015764369584852833, -0.31062937026679327, -0.562666951622192, -0.8984947637863987]}", JsonObject.class),
//                gson.fromJson("{\"id\": 7, \"vector\": [-0.33445148015177995, -0.2567135004164067, 0.8987539745369246, 0.9402995886420709, 0.5378064918413052]}", JsonObject.class),
//                gson.fromJson("{\"id\": 8, \"vector\": [0.39524717779832685, 0.4000257286739164, -0.5890507376891594, -0.8650502298996872, -0.6140360785406336]}", JsonObject.class),
//                gson.fromJson("{\"id\": 9, \"vector\": [0.5718280481994695, 0.24070317428066512, -0.3737913482606834, -0.06726932177492717, -0.6980531615588608]}", JsonObject.class)
//        );
//
//        InsertReq insertReq = InsertReq.builder()
//                .collectionName(COLLECTION_NAME)
//                .data(data)
//                .build();
//        InsertResp insertResp = milvusClient.insert(insertReq);
//        Thread.sleep(5000); // 等待 5 秒
//        System.out.println(insertResp);
//
//        FloatVec queryVector = new FloatVec(new float[]{0.3580376395471989f, -0.6023495712049978f, 0.18414012509913835f, -0.26286205330961354f, 0.9029438446296592f});
//        SearchReq searchReq = SearchReq.builder()
//                .collectionName(COLLECTION_NAME)
//                .data(Collections.singletonList(queryVector))
//                .annsField("vector")
//                .topK(3)
//                .build();
//
//        SearchResp searchResp = milvusClient.search(searchReq);
//
//        List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
//        for (List<SearchResp.SearchResult> results : searchResults) {
//            System.out.println("TopK results:");
//            for (SearchResp.SearchResult result : results) {
//                System.out.println(result);
//            }
//        }
    }
}
