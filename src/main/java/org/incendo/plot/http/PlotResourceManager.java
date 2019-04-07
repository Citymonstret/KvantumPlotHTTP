package org.incendo.plot.http;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class PlotResourceManager {

    private final Map<String, Resource> resources = new HashMap<>();
    private Resource defaultResource;

    Set<String> getResources() {
        return Collections.unmodifiableSet(this.resources.keySet());
    }

    /**
     * Add a resource
     *
     * @param resource resource, cannot be null
     */
    void addResource(final Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        this.resources.put(resource.toString(), resource);
    }

    /**
     * Get a resource from its name
     *
     * @param name resource name, may not be null
     */
    Resource getResource(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Resource name cannot be null");
        }
        return this.resources.get(name);
    }

    void setDefault(@Nullable final Resource defaultResource) {
        this.defaultResource = defaultResource;
    }

    @Nullable Resource getDefaultResource() {
        return this.defaultResource;
    }

}
