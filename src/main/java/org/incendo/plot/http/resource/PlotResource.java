package org.incendo.plot.http.resource;

import com.github.intellectualsites.plotsquared.json.JSONArray;
import com.github.intellectualsites.plotsquared.json.JSONObject;
import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.object.BlockLoc;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotCluster;
import com.github.intellectualsites.plotsquared.plot.object.PlotId;
import com.github.intellectualsites.plotsquared.plot.util.UUIDHandler;
import org.incendo.plot.http.Resource;
import xyz.kvantum.server.api.request.AbstractRequest;
import xyz.kvantum.server.api.session.ISession;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlotResource extends Resource {

    @Override public String toString() {
        return "plots";
    }

    @Override protected Object getResult(AbstractRequest request, ISession session) {
        final Map<String, String> parameters = request.getQuery().getParameters();
        final String world = parameters.get("world");
        final String area = parameters.get("area");

        PlotId id = null;
        final String idStr = parameters.get("id");
        if (idStr != null) {
            id = PlotId.fromString(idStr);
        }
        Collection<Plot> plots = PlotSquared.get().getPlots();

        if (id != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                if (!plot.getId().equals(id)) {
                    i.remove();
                }
            }
        }
        if (area != null) {
            plots.removeIf(plot -> !plot.getArea().toString().equals(area));
        }
        if (world != null) {
            plots.removeIf(plot -> !plot.getArea().worldname.equals(world));
        }
        if (plots.size() == 0) {
            return null;
        }
        UUID uuid = getUUID(parameters.get("owner"));
        if (uuid != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                if ((plot.owner == null) || !plot.owner.equals(uuid)) {
                    i.remove();
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        uuid = getUUID(parameters.get("members"));
        if (uuid != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                if (!plot.getMembers().contains(uuid)) {
                    i.remove();
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        uuid = getUUID(parameters.get("allowed"));
        if (uuid != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                if (!plot.isAdded(uuid)) {
                    i.remove();
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        uuid = getUUID(parameters.get("trusted"));
        if (uuid != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                if (!plot.getTrusted().contains(uuid)) {
                    i.remove();
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        uuid = getUUID(parameters.get("denied"));
        if (uuid != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                if (!plot.getDenied().contains(uuid)) {
                    i.remove();
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        final String clusterName = parameters.get("cluster");
        if (clusterName != null) {
            final Iterator<Plot> i = plots.iterator();
            while (i.hasNext()) {
                final Plot plot = i.next();
                PlotCluster cluster = plot.getCluster();
                if (cluster == null || !cluster.getName().equals(clusterName)) {
                    i.remove();
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        final String alias = parameters.get("alias");
        if (alias != null) {
            plots.removeIf(plot -> !alias.equals(plot.getSettings().getAlias()));
        }
        if (plots.size() == 0) {
            return null;
        }
        final String mergedStr = parameters.get("merged");
        if (alias != null) {
            final Iterator<Plot> i = plots.iterator();
            if (mergedStr.equals("true")) {
                while (i.hasNext()) {
                    final Plot plot = i.next();
                    if (!plot.isMerged()) {
                        i.remove();
                    }
                }
            } else {
                while (i.hasNext()) {
                    final Plot plot = i.next();
                    if (plot.isMerged()) {
                        i.remove();
                    }
                }
            }
        }
        if (plots.size() == 0) {
            return null;
        }
        final JSONArray plotsObj = new JSONArray();
        for (final Plot plot : plots) {
            plotsObj.put(serializePlot(plot));
        }
        return plotsObj;
    }

    private JSONObject serializePlot(final Plot plot) {
        final JSONObject obj = new JSONObject();
        final JSONObject id = new JSONObject();
        id.put("x", plot.getId().x);
        id.put("y", plot.getId().y);
        obj.put("id", id);
        obj.put("area", plot.getArea().toString());
        obj.put("world", plot.getArea().worldname);
        obj.put("ownerUUID", plot.owner);
        obj.put("ownerName", plot.owner == null ? "" : UUIDHandler.getName(plot.owner));
        obj.put("flags", getArray(plot.getSettings().flags.values()));
        obj.put("trusted", getArray(plot.getTrusted()));
        obj.put("members", getArray(plot.getMembers()));
        obj.put("denied", getArray(plot.getDenied()));
        obj.put("merged", plot.isMerged());
        obj.put("alias", plot.getSettings().getAlias());

        final BlockLoc home = plot.getSettings().getPosition();
        final JSONObject homeObj = new JSONObject();
        if (home == null) {
            homeObj.put("x", "");
            homeObj.put("y", "");
            homeObj.put("z", "");
        } else {
            homeObj.put("x", home.x);
            homeObj.put("y", home.y);
            homeObj.put("z", home.z);
        }
        obj.put("home", homeObj);
        return obj;
    }

}
