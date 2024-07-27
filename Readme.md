## Spring shardcake

Sample repo trying to get spring to play nicely with shardcake

## Run instructions

Run the server

```
./gradle bootRun
```

Test endpoints (warning - all of them just return a static json response. Look at the logs to see whats happening)

- [Very simple endpoint - just returns static response. No effects](http://localhost:8080/rest/test)
- [Simple effect endpoint - just logs using ZIO effect. No dependencies](http://localhost:8080/rest/basic-effect)
- [Uses shardcake to run the guild program and return a static response](http://localhost:8080/rest/guild)
