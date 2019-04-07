package org.incendo.plot.http;

import xyz.kvantum.server.api.orm.annotations.KvantumConstructor;
import xyz.kvantum.server.api.orm.annotations.KvantumField;
import xyz.kvantum.server.api.orm.annotations.KvantumInsert;
import xyz.kvantum.server.api.orm.annotations.KvantumObject;

@KvantumObject public final class PlotHttpRequest {

    @KvantumField private final String resource;

    @KvantumConstructor public PlotHttpRequest(@KvantumInsert("resource") final String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return this.resource;
    }

}
