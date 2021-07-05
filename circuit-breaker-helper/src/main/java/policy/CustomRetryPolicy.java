package policy;

import org.springframework.classify.Classifier;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.ResourceAccessException;

public class CustomRetryPolicy extends ExceptionClassifierRetryPolicy
{
    public CustomRetryPolicy()
    {
        final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);

        this.setExceptionClassifier((Classifier<Throwable, RetryPolicy>) classifiable -> {
            if ( classifiable instanceof ResourceAccessException)
            {
                return simpleRetryPolicy;
            }
            return new NeverRetryPolicy();
        });
    }
}
