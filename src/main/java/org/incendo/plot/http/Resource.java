package org.incendo.plot.http;

import com.github.intellectualsites.plotsquared.json.JSONArray;
import com.github.intellectualsites.plotsquared.json.JSONObject;
import com.github.intellectualsites.plotsquared.plot.util.UUIDHandler;
import xyz.kvantum.server.api.request.AbstractRequest;
import xyz.kvantum.server.api.response.Header;
import xyz.kvantum.server.api.response.Response;
import xyz.kvantum.server.api.session.ISession;

import java.util.Collection;
import java.util.UUID;

/**
 * Stolen from https://github.com/boy0001/PlotHTTP/blob/breaking/src/main/java/com/boydti/plothttp/object/Resource.java
 * @author boy0001
 */
public abstract class Resource {

    @Override
    public abstract String toString();

    protected abstract Object getResult(AbstractRequest request, ISession session);

    final void getResponse(final AbstractRequest request, final Response response) {
        final ISession session = request.getSession(); // Get, or create a session

        // Get a the result of the resource
        final Object result = getResult(request, session);

        // Return '404 NOT FOUND' - if resource returns null
        if (result == null) {
            response.getHeader().setStatus(Header.STATUS_NOT_FOUND);
            response.setResponse(String.format("No resource generated (%s)", this.toString()));
            return;
        }

        boolean empty;
        if (result instanceof JSONObject) {
            empty = ((JSONObject) result).length() == 0;
        }  else if (result instanceof JSONArray) {
            empty = ((JSONArray) result).length() == 0;
        } else {
            empty = result.toString().isEmpty();
        }

        // Return an empty result
        if (empty) {
            this.setResponse(response, "[]");
            return;
        }

        this.setResponse(response, result.toString());
    }

    private void setResponse(final Response response, final String json) {
        response.getHeader().set(Header.HEADER_CONTENT_TYPE, Header.CONTENT_TYPE_JSON);
        response.setResponse(json);
    }

    protected JSONArray getArray(final Collection<?> collection) {
        final JSONArray array = new JSONArray();
        for (final Object object : collection) {
            array.put(object.toString());
        }
        return array;
    }

    protected JSONArray getArray(final Object[] collection) {
        final JSONArray array = new JSONArray();
        for (final Object object : collection) {
            array.put(object.toString());
        }
        return array;
    }

    protected UUID getUUID(final String name) {
        if (name == null) {
            return null;
        }
        final UUID uuid = UUIDHandler.getUUID(name, null);
        if (uuid != null) {
            return uuid;
        }
        try {
            return UUID.fromString(name);
        } catch (final Exception e) {
            return null;
        }
    }

}
