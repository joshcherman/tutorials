# GraphQl to PySpark translation POC

## Run Locally

To start the Spring Boot application:
```
./gradlew bootRun
```

## Some test queries

Simple single table query with a filter and projections:
```
curl 'http://localhost:8080/graphql' -H 'Content-Type: application/json' -H 'Accept: application/json' --data-binary '{"query":"{  bookById(bookId: 2){    bookId  name }}"}'
```

Two table join, filter and projections:
```
curl 'http://localhost:8080/graphql' -H 'Content-Type: application/json' -H 'Accept: application/json' --data-binary '{"query":"{  bookById(bookId: 2){    bookId    name    pageCount    author{      firstName      lastName    }  }}"}'
```

Custom enrichment, three table join, filter and projections:
```
curl 'http://localhost:8080/graphql' -H 'Content-Type: application/json' -H 'Accept: application/json' --data-binary '{"query":"{  bookById(bookId: 2){    bookId    name    pageCount    author{      firstName      lastName    } bookEnrichments{ bookWordCount }  }}"}'
```