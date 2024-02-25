package org.eustrosoft.experimental;

import org.eustrosoft.core.request.Request;
import org.eustrosoft.core.response.Response;

import java.util.concurrent.Future;
import java.util.function.Function;

public interface RequestSender {
    Future<Response> send(Function<Request, String> requestFunction, Request request);
}
