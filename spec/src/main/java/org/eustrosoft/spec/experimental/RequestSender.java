package org.eustrosoft.spec.experimental;

import org.eustrosoft.spec.interfaces.Request;
import org.eustrosoft.spec.interfaces.Response;

import java.util.concurrent.Future;
import java.util.function.Function;

public interface RequestSender {
    Future<Response> send(Function<Request, String> requestFunction, Request request);
}
