# search-aggregator

## Evaluation Task

### Initial requirements:
  Create a webservice comprising of:
  - **endpoints** for submitting search query and retrieving stream of search results
  - **service** for parallel and asynchronous search throu multiple search engines

  Endpoints:
  
  1. POST **/task** accepting following JSON: 
```json
    {
      "term": "search term string", 
      "priority": 1, 
      "num": 10
    }
```
where priority and num are integers. 
Service should save task into persistent storage and return JSON with task identifier: 
```json
    {
    "id": 42
    }
```
  
  2. GET **/results/?from=:id** returning SSE stream of following JSON objects:  
  
  ```json
    {
      "id": 42, 
      "results": [
        "http://example.com",
        "http://github.com"
      ]
    }
```
  
starting from task identifier in a GET request.
  
  
  Task Processing Rules:
  1. Tasks are sorted by priority (highest priority first)
  2. Submitted search 'term' is is evaluated against number of configured search engines
  3. Number of search results per search engine should be limited to 'num' parameter
  4. Results from all search engines are combined into single list excluding duplicates
  5. It is should be possible to add search engines
  6. Each search engine should be capable of limiting number of simultaneous requests
  
  
