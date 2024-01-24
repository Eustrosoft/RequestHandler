package org.eustrosoft.handlers;

import org.eustrosoft.spec.Request;
import org.eustrosoft.spec.Response;

import java.util.concurrent.Future;
import java.util.function.Function;

public interface RequestSender {
    Future<Response> send(Function<Request, String> requestFunction, Request request);
}
