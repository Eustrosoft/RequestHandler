package org.eustrosoft.handlers;

import org.eustrosoft.handlers.cms.dto.Request;
import org.eustrosoft.handlers.cms.dto.Response;

import java.util.concurrent.Future;
import java.util.function.Function;

public interface RequestSender {
    Future<Response> send(Function<Request, String> requestFunction, Request request);
}
