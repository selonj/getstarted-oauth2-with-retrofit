package com.selonj.gson.enclosed.dsl;

import com.selonj.gson.enclosed.EnclosingTypeAdapterFactory;

/**
 * Created by Administrator on 2016-03-31.
 */
public class Enclosing implements EnclosingClause {
    private EnclosingTypeAdapterFactory factory = new EnclosingTypeAdapterFactory();

    public Enclosing(Class enclosingType) {
        factory.setEnclosingType(enclosingType);
    }

    public static EnclosingClause with(Class enclosingType) {
        return new Enclosing(enclosingType);
    }

    @Override
    public GsonClause on(String property) {
        factory.setEnclosingName(property);
        return factory;
    }
}
