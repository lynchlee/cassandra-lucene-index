/*
 * Copyright 2014, Stratio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.cassandra.lucene.search;

import com.google.common.base.Objects;
import com.stratio.cassandra.lucene.schema.Schema;
import com.stratio.cassandra.lucene.search.condition.Condition;
import com.stratio.cassandra.lucene.search.sort.Sort;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;

/**
 * Class representing an Lucene index search. It is formed by an optional querying {@link Condition} and an optional
 * filtering {@link Condition}. It can be translated to a Lucene {@link Query} using a {@link Schema}.
 *
 * @author Andres de la Pena {@literal <adelapena@stratio.com>}
 */
public class Search {

    /** he {@link Condition} for querying, maybe {@code null} meaning no querying. */
    private final Condition queryCondition;

    /** The {@link Condition} for filtering, maybe {@code null} meaning no filtering. */
    private final Condition filterCondition;

    /**
     * The {@link Sort} for the query. Note that is the order in which the data will be read before querying, not the
     * order of the results after querying.
     */
    private final Sort sort;

    /**
     * Returns a new {@link Search} composed by the specified querying and filtering conditions.
     *
     * @param queryCondition  The {@link Condition} for querying, maybe {@code null} meaning no querying.
     * @param filterCondition The {@link Condition} for filtering, maybe {@code null} meaning no filtering.
     * @param sort            The {@link Sort} for the query. Note that is the order in which the data will be read
     *                        before querying, not the order of the results after querying.
     */
    public Search(Condition queryCondition, Condition filterCondition, Sort sort) {
        this.queryCondition = queryCondition;
        this.filterCondition = filterCondition;
        this.sort = sort;
    }

    /**
     * Returns {@code true} if the results must be ordered by relevance. If {@code false}, then the results are sorted
     * by the natural Cassandra's order. Results must be ordered by relevance if the querying condition is not {code
     * null}.
     *
     * Relevance is used when the query condition is set, and it is not used when only the clusteringKeyFilter condition
     * is set.
     *
     * @return {@code true} if the results must be ordered by relevance. If {@code false}, then the results must be
     * sorted by the natural Cassandra's order.
     */
    public boolean usesRelevanceOrSorting() {
        return queryCondition != null || sort != null;
    }

    /**
     * Returns {@code true} if this search uses Lucene relevance formula, {@code false} otherwise.
     *
     * @return {@code true} if this search uses Lucene relevance formula, {@code false} otherwise.
     */
    public boolean usesRelevance() {
        return queryCondition != null;
    }

    /**
     * Returns {@code true} if this search uses field sorting, {@code false} otherwise.
     *
     * @return {@code true} if this search uses field sorting, {@code false} otherwise.
     */
    public boolean usesSorting() {
        return sort != null;
    }

    /**
     * Returns the field sorting to be used, maybe {@code null} meaning no field sorting.
     *
     * @return The field sorting to be used, maybe {@code null} meaning no field sorting.
     */
    public Sort getSort() {
        return this.sort;
    }

    /**
     * Returns the Lucene {@link SortField}s represented by this using the specified {@link Schema}. Maybe {@code null}
     * meaning no sorting.
     *
     * @param schema A {@link Schema}.
     * @return The Lucene {@link SortField}s represented by this using {@code schema}.
     */
    public SortField[] sortFields(Schema schema) {
        return sort == null ? null : sort.sortFields(schema);
    }

    /**
     * Returns the Lucene {@link Query} represented by this using the specified {@link Schema}. Maybe {@code null}
     * meaning no query.
     *
     * @param schema A {@link Schema}.
     * @return The Lucene {@link Query} represented by this using the specified {@link Schema}
     */
    public Query query(Schema schema) {
        return queryCondition == null ? null : queryCondition.query(schema);
    }

    /**
     * Returns the Lucene filtering {@link Query} represented by this using the specified {@link Schema}. Maybe {@code
     * null} meaning no filtering query.
     *
     * @param schema A {@link Schema}.
     * @return The Lucene filtering {@link Query} represented by this using the specified {@link Schema}
     */
    public Query filter(Schema schema) {
        return filterCondition == null ? null : filterCondition.query(schema);
    }

    /**
     * Validates this {@link Search} against the specified {@link Schema}.
     *
     * @param schema A {@link Schema}.
     */
    public void validate(Schema schema) {
        if (queryCondition != null) {
            query(schema);
        }
        if (filterCondition != null) {
            filter(schema);
        }
        if (sort != null) {
            sort.sortFields(schema);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("queryCondition", queryCondition)
                      .add("filterCondition", filterCondition)
                      .add("sort", sort)
                      .toString();
    }
}