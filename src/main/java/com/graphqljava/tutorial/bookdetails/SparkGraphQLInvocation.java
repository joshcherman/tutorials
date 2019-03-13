package com.graphqljava.tutorial.bookdetails;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Internal;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.CompletableFuture;

@Primary
@Component
@Internal
public class SparkGraphQLInvocation implements GraphQLInvocation {

    @Autowired
    private GraphQL graphQL;

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        String gQLQuery = invocationData.getQuery();
        int indexToInsert = gQLQuery.indexOf("{", gQLQuery.indexOf("{") + 1);
        String modifiedGQLQuery = insertString(gQLQuery, "sparkQuery", indexToInsert);

        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(modifiedGQLQuery)
                .operationName(invocationData.getOperationName()).variables(invocationData.getVariables()).build();
        return graphQL.executeAsync(executionInput);
    }

    private static String insertString(String originalString, String stringToBeInserted, int index) {
        StringBuffer newString = new StringBuffer(originalString);
        newString.insert(index + 1, stringToBeInserted);
        return newString.toString();
    }

}