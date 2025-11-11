package netcentral.transceiver.agw.auth;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.authentication.DefaultAuthorizationExceptionHandler;
import io.micronaut.security.config.RedirectConfiguration;
import io.micronaut.security.config.RedirectService;
import io.micronaut.security.errors.PriorToLoginPersistence;
import jakarta.inject.Singleton;


@Singleton 
@Replaces(DefaultAuthorizationExceptionHandler.class) 
public class DefaultAuthorizationExceptionHandlerReplacement extends DefaultAuthorizationExceptionHandler {

    public DefaultAuthorizationExceptionHandlerReplacement(
            ErrorResponseProcessor<?> errorResponseProcessor,
            RedirectConfiguration redirectConfiguration,
            RedirectService redirectService,
            @Nullable PriorToLoginPersistence<?, ?> priorToLoginPersistence
    ) {
        super(errorResponseProcessor, redirectConfiguration, redirectService, priorToLoginPersistence);
    }

    @Override
    protected MutableHttpResponse<?> httpResponseWithStatus(HttpRequest<?> request,
                                                            AuthorizationException e) {
        if (e.isForbidden()) {
            return HttpResponse.status(HttpStatus.FORBIDDEN);
        }
        return HttpResponse.status(HttpStatus.UNAUTHORIZED);
    }
}