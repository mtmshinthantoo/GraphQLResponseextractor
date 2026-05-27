package com.testshin.extractor;

import com.testshin.extractor.extractor.GraphQLRowExtractor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class    Main {

    public static void main(String[] args) {

        String json = """
                {
                     "data": {
                         "articles": {
                             "edges": [
                                 {
                                     "node": {
                                         "id": "gid://shopify/Article/560974561460",
                                         "blog": {
                                             "id": "gid://shopify/Blog/74552574132"
                                         },
                                         "author": {
                                             "name": "article1"
                                         },
                                         "body": "<h1>I like articles</h1>\\n<p><strong>Yea</strong>, I like posting them through <span class=\\"caps\\">REST</span>.</p>",
                                         "createdAt": "2024-07-26T06:23:57Z",
                                         "handle": "my-new-article-title-1",
                                         "image": null,
                                         "publishedAt": "2011-03-24T15:45:47Z",
                                         "summary": null,
                                         "tags": [
                                             "Has Been Tagged",
                                             "This Post"
                                         ],
                                         "templateSuffix": null,
                                         "title": "My new Article title",
                                         "updatedAt": "2024-07-26T06:23:57Z",
                                         "isPublished": true,
                                         "customAttributes": [
                                             {
                                                 "key": "engraving",
                                                 "value": "Happy Birthday, chit ka lay"
                                             },
                                             {
                                                 "key": "gift_wrap",
                                                 "value": "yes"
                                             }
                                         ]
                                     }
                                 },
                                 {
                                     "node": {
                                         "id": "gid://shopify/Article/560974234245",
                                         "blog": {
                                             "id": "gid://shopify/Blog/745525745353"
                                         },
                                         "author": {
                                             "name": "article2"
                                         },
                                         "body": "<h1>I like articles</h1>\\n<p><strong>Yea</strong>, I like posting them through <span class=\\"caps\\">REST</span>.</p>",
                                         "createdAt": "2024-07-26T06:23:57Z",
                                         "handle": "my-new-article-title-2",
                                         "image": null,
                                         "publishedAt": "2021-03-24T15:45:47Z",
                                         "summary": null,
                                         "tags": [
                                             "Has Been Tagged",
                                             "This Post",
                                             "New Tag",
                                             "Shin Thant Oo"
                                         ],
                                         "templateSuffix": null,
                                         "title": "My new Article title",
                                         "updatedAt": "2024-07-26T06:23:57Z",
                                         "isPublished": true,
                                         "customAttributes": [
                                             {
                                                 "key": "celebration",
                                                 "value": "Happy 2nd Anniversary, chit ka lay"
                                             },
                                             {
                                                 "key": "gift",
                                                 "value": "Happy Birthday"
                                             },
                                             {
                                                 "key": "gift_wrap",
                                                 "value": "yes"
                                             }
                                         ]
                                     }
                                 }
                             ],
                             "pageInfo": {
                                 "hasNextPage": false,
                                 "hasPreviousPage": false,
                                 "startCursor": "eyJsYXN0X2lkIjo1NjA5NzQ1NjE0NjAsImxhc3RfdmFsdWUiOiI1NjA5NzQ1NjE0NjAifQ==",
                                 "endCursor": "eyJsYXN0X2lkIjo1NjA5NzQ1NjE0NjAsImxhc3RfdmFsdWUiOiI1NjA5NzQ1NjE0NjAifQ=="
                             }
                         }
                     },
                     "extensions": {
                         "cost": {
                             "requestedQueryCost": 46,
                             "actualQueryCost": 3,
                             "throttleStatus": {
                                 "maximumAvailable": 2000.0,
                                 "currentlyAvailable": 1997,
                                 "restoreRate": 100.0
                             }
                         }
                     }
                 }
""";

        String rootPath = "data.articles.edges";


        Map<String, String> mappings = new LinkedHashMap<>();

        mappings.put("articleId", "id");
        mappings.put("blogId", "blog.id");
        mappings.put("authorName", "author.name");
        mappings.put("tags", "tags");
        mappings.put("Key", "customAttributes.key");
        mappings.put("Value", "customAttributes.value");


        GraphQLRowExtractor extractor = new GraphQLRowExtractor();

        List<Map<String, String>> rows =
                extractor.extractRows(
                        json,
                        rootPath,
                        mappings
                );

        System.out.println("Rows Count = " + rows.size());

        for (Map<String, String> row : rows) {
            System.out.println(row);
        }
    }
}