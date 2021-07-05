package listener;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

import java.time.Instant;
import java.util.Date;

public class CustomRetryListenerSupport extends RetryListenerSupport
{
    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable)
    {
        System.out.println(Date.from(Instant.now()) + " onClose");
        super.close(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable)
    {
        System.out.println(Date.from(Instant.now()) + " onError");
        super.onError(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback)
    {
        System.out.println(Date.from(Instant.now()) + " onOpen");

        return super.open(context, callback);
    }
}