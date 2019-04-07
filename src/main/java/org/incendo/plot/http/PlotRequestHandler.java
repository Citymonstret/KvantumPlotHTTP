package org.incendo.plot.http;

import xyz.kvantum.server.api.orm.KvantumObjectFactory;
import xyz.kvantum.server.api.orm.KvantumObjectParserResult;
import xyz.kvantum.server.api.request.AbstractRequest;
import xyz.kvantum.server.api.response.Header;
import xyz.kvantum.server.api.response.Response;
import xyz.kvantum.server.api.util.ParameterScope;
import xyz.kvantum.server.api.views.annotatedviews.ViewMatcher;

class PlotRequestHandler {

    private final KvantumObjectFactory<PlotHttpRequest> plotRequestFactory =
        KvantumObjectFactory.from(PlotHttpRequest.class);
    private final PlotHttp plotHttp;

    PlotRequestHandler(final PlotHttp plotHttp) {
        if (plotHttp == null) {
            throw new IllegalArgumentException("PlotHttp may not be null");
        }
        this.plotHttp = plotHttp;
    }

    @ViewMatcher(filter = "")
    public void handleRequest(final AbstractRequest request, final Response response) {
        try {
            final KvantumObjectParserResult<PlotHttpRequest> parserResult =
                plotRequestFactory.build(ParameterScope.GET).parseRequest(request);
            if (parserResult.getError() != null) {
                response.getHeader().setStatus(Header.STATUS_BAD_REQUEST);
                response.setResponse(parserResult.getError().getCause());
                return;
            }
            final PlotHttpRequest plotRequest = parserResult.getParsedObject();
            final Resource resource = this.plotHttp.getResourceManager().getResource(plotRequest.getResource());
            if (resource == null) {
                response.getHeader().setStatus(Header.STATUS_NOT_FOUND);
                response.setResponse(String.format("No such resource: %s", plotRequest.getResource()));
                return;
            }
            resource.getResponse(request, response);
        } catch (final Throwable exception) {
            response.getHeader().setStatus(Header.STATUS_INTERNAL_ERROR);
            response.setResponse(exception.getMessage());
        }
    }

}
