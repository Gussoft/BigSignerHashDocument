package utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class S3Util {

    private static AmazonS3 amazonS3;

    public static AmazonS3 getAmazonS3() {
        if (amazonS3 != null) {
            return amazonS3;
        }
        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        String s3Bucket = System.getenv("AWS_BUCKET_OUT");

        if ((accessKey != null) && (secretKey != null)) {
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            amazonS3 = new AmazonS3Client(awsCredentials);
        }
        return amazonS3;
    }

    public static void uploadFile(String fileName, File file, Boolean isPublic) {
        String s3Bucket = System.getenv("AWS_BUCKET_OUT");
        //Logger.info(s3Bucket);
        PutObjectRequest putObjectRequest = new PutObjectRequest(s3Bucket, fileName, file);
        if (isPublic) {
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
        }
        getAmazonS3().putObject(putObjectRequest); // upload fil
    }

    public static File getFile(String path, String prefixFileName, String extensionFileName) throws IOException {
        //String s3Bucket = ConfigFactory.load().getString("aws.s3.bucket");
        String s3Bucket = System.getenv("AWS_BUCKET_IN");
        S3Object fullObject = getAmazonS3().getObject(new GetObjectRequest(s3Bucket, path));
        InputStream in = fullObject.getObjectContent();

        final File tempFile = File.createTempFile(prefixFileName, extensionFileName);
//        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }
}