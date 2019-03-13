package com.graphqljava.tutorial.bookdetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.Internal;
import graphql.spring.web.servlet.ExecutionResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.Map;

@Primary
@Component
@Internal
public class SparkExecutionResultHandler implements ExecutionResultHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Object handleExecutionResult(CompletableFuture<ExecutionResult> executionResultCF) {
        Map<String, Object> data = (Map<String, Object>)executionResultCF.thenApply(ExecutionResult::toSpecification).join().get("data");
        Map<String, Object> bookById = (Map<String, Object>)data.get("bookById");
        return bookById.get("sparkQuery");
    }
}