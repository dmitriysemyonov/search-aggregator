# search-aggregator

## Evaluation Task

### Initial requirements:
  Create a webservice comprising of:
  - **endpoints** for submitting search query and retrieving stream of search results
  - **service** for parallel and asynchronous search through multiple search engines

  Endpoints:
  
  1. POST **/task** accepting following JSON: 
```json
    {
      "term": "how to geek?", 
      "priority": 1, 
      "num": 10
    }
```
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
        "http://stackoverflow.com",
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
  
  
### Running Application

- Run instance of Cassandra. For example, with Docker:

```shell
docker pull cassandra
docker run -d -p 127.0.0.1:9042:9042 -p 127.0.0.1:9160:9160 -p 127.0.0.1:9142:9042  --name cassandra -it cassandra
```

- Create table
````shell
docker exec -it cassandra /bin/bash
cqlsh
create table goog.search ( id bigint primary key, term text, priority int, num bigint, result set<text> );
````






search flow
- controller receives search request 
- request is stored into database with id 42
- persistent search actor created with id 42
- controller sends command 'search' to search actor
- search actor dispatches calls to search engines
- search engines sends commands to search actor with search results
- search actor stores internal state with data from search engines


- controller receives request for search results
- controller looks up persistent actors with matching id
- if search actor already created continue
- if search actor not found then create search actor and restore it's state
- controller asks for search results

- search actor yield events to controller with search results