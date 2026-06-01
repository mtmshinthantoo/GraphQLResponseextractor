# Generic JSON API Exporter

This project turns API response JSON into rows using mapping JSON. When an API response changes, the Java code should stay stable; add or edit the response file and mapping entry instead.

## Structure

```text
src/main/java/com/testshin/extractor
  Main.java
  config/
    ExtractionConfig.java
    FieldMapping.java
    MappingLoader.java
    StepConfig.java
    StepType.java
  engine/
    JsonPath.java
    JsonRowExtractor.java
  exception/
    ExtractionException.java

src/main/resources
  mappings/
    export-mappings.json
  responses/
    article-response.json
    order-discount-codes-response.json
    order-items-response.json
```

## Mapping Steps

- `value`: read one value from a JSON path
- `flatten`: join array values into one column
- `expand`: turn an array into multiple rows
- `join`: match one array against another array

## Add A New Object

1. Add the response JSON under `src/main/resources/responses`.
2. Add one top-level mapping entry in `src/main/resources/mappings/export-mappings.json`.
3. Set `rootPath` to the list location.
4. Set `itemPath` to the object inside each list item, usually `node` for Shopify GraphQL `edges`.
5. Add `steps` for the columns you want.

Example:

```json
"Products": {
  "rootPath": "data.products.edges",
  "itemPath": "node",
  "steps": [
    {
      "type": "value",
      "column": "productId",
      "path": "id"
    },
    {
      "type": "flatten",
      "column": "tags",
      "path": "tags",
      "separator": ", "
    }
  ]
}
```

## Run

Default mapping from `Main.java`:

```powershell
mvn -q exec:java "-Dexec.mainClass=com.testshin.extractor.Main"
```

Specific mapping and response:

```powershell
mvn -q exec:java "-Dexec.mainClass=com.testshin.extractor.Main" "-Dexec.args=OrderLineItems src/main/resources/responses/order-items-response.json src/main/resources/mappings/export-mappings.json"
```

Argument order:

```text
mappingName responseJsonPath mappingJsonPath
```
