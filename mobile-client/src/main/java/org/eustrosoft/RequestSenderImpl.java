package org.eustrosoft;

import org.eustrosoft.handlers.RequestSender;
import org.eustrosoft.spec.Request;
import org.eustrosoft.spec.Response;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RequestSenderImpl implements RequestSender {
    @Override
    public CompletableFuture<Response> send(Function<Request, String> requestFunction, Request request) {
        String json = requestFunction.apply(request);
        // Send realization here

        return null;
    }
}
