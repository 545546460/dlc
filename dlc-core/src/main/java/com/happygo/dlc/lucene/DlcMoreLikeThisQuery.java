package com.happygo.dlc.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

import java.io.IOException;

/**
 * Created by ACER on 2017/8/7.
 */
public class DlcMoreLikeThisQuery extends Query {

    /**
     * Expert: called to re-write queries into primitive queries. For example,
     * a PrefixQuery will be rewritten into a BooleanQuery that consists
     * of TermQuerys.
     *
     * @param reader
     */
    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        reader.maxDoc();
        return super.rewrite(reader);
    }

    /**
     * Prints a query to a string, with <code>field</code> assumed to be the
     * default field and omitted.
     *
     * @param field
     */
    @Override
    public String toString(String field) {
        return null;
    }
}
