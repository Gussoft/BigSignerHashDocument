package utils;

import com.amazonaws.auth.AWSCredentials;

public class AwsServices {

    static final String ACCESS_KEY = System.getenv("AWS_ACCESS_KEY");
    static final String ACCESS_SECRET = System.getenv("AWS_SECRET_KEY");

    public static AWSCredentials connectAws() {
        AWSCredentials credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return ACCESS_KEY;
            }

            @Override
            public String getAWSSecretKey() {
                return ACCESS_SECRET;
            }
        };
        return credentials;
    }
}