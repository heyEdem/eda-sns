package org.edem.edasamsns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class LambdaHandler implements RequestHandler<S3Event, Void> {
    private final String SNS_TOPIC_ARN = System.getenv("SNS_TOPIC_ARN");
    private final SnsClient snsClient = SnsClient.create();

    @Override
    public Void handleRequest(S3Event event, Context context) {
        try {
            for (var record : event.getRecords()) {
                String bucket = record.getS3().getBucket().getName();
                String key = record.getS3().getObject().getKey();
                String message = String.format("New object uploaded to %s: %s", bucket, key);
                PublishRequest request = PublishRequest.builder()
                        .topicArn(SNS_TOPIC_ARN)
                        .message(message)
                        .build();
                snsClient.publish(request);
            }
        }
        catch (Exception e){
            context.getLogger().log("Something went wrong " + e);
        }
        return null;
    }
}
