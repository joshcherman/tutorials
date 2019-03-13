package com.graphqljava.tutorial.bookdetails;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
public class GraphQLDataFetchers {

        private static Map<String, String> joinConditions = ImmutableMap.of("Book-Author", "authorId", "Author-Book",
                        "authorId", "Book-BookEnrichments", "bookId");

        public DataFetcher getBookByIdDataFetcher() {
                return dataFetchingEnvironment -> {
                        StringBuilder sb = new StringBuilder();
                        List<String> tablesUsed = new ArrayList<String>();
                        List<String> selectFieldNames = new ArrayList<String>();

                        // Figure out what tables are used in the query
                        tablesUsed.add("Book"); // add root table
                        dataFetchingEnvironment.getSelectionSet().getFields().forEach(x -> {
                                {
                                        if (!x.getSelectionSet().getFields().isEmpty()) {
                                                tablesUsed.add(x.getFieldDefinition().getType().getName());
                                        }
                                }
                        });
                        // Code to import packages
                        sb.append("from pyspark.sql.functions import *\nfrom pyspark.sql.types import *\nfrom pyspark.sql import *\n"); // base
                                                                                                                                        // imports
                        // Code to read tables
                        for (String table : tablesUsed) {
                                if (table.contains("Enrichment")) {
                                        sb.append("from " + table.toLowerCase() + " import *\n");
                                        if (tablesUsed.size() == 1) {
                                                sb.append("df = " + table.toLowerCase() + ".run_enrichment();\n");
                                        } else {
                                                sb.append(table + " = " + table.toLowerCase() + ".run_enrichment();\n");
                                        }

                                } else {
                                        if (tablesUsed.size() == 1) {
                                                sb.append("df = spark.read.redshift(\"" + table + "\")\n");
                                        } else {
                                                sb.append(table + " = spark.read.redshift(\"" + table + "\")\n");
                                        }
                                }
                        }

                        // Code to join tables
                        if (tablesUsed.size() > 1) {
                                sb.append("df = " + tablesUsed.get(0) + ".join(" + tablesUsed.get(1) + ", [\""
                                                + joinConditions.get(tablesUsed.get(0) + "-" + tablesUsed.get(1))
                                                + "\"], \"left_outer\")\n");
                                for (int i = 2; i < tablesUsed.size(); i++) {
                                        sb.append("df = df.join(" + tablesUsed.get(i) + ", [\""
                                                        + joinConditions.get(
                                                                        tablesUsed.get(0) + "-" + tablesUsed.get(i))
                                                        + "\"], \"left_outer\")\n");
                                }
                        }

                        // Figure out what columns are used in the query
                        dataFetchingEnvironment.getSelectionSet().getFields().forEach(x -> {
                                {
                                        if (x.getSelectionSet().getFields().isEmpty()
                                                        && !x.getName().equalsIgnoreCase("sparkQuery")) {
                                                selectFieldNames.add(x.getName());
                                        }
                                }
                        });

                        // Code to select columns
                        sb.append("df = df.select(\"" + String.join("\",\"", selectFieldNames) + "\")\n");

                        // Code to generate filters
                        int bookId = dataFetchingEnvironment.getArgument("bookId");
                        sb.append("df = df.where(col(\"bookId\") == " + bookId + ")\n");

                        // Code to write output
                        sb.append("df = df.write.datalake()\n");

                        return new HashMap<String, Object>() {
                                {
                                        put("sparkQuery", sb.toString());
                                }
                        };
                };
        }
}
