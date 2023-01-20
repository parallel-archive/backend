package hu.codeandsoda.osa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig {

    @Value("${jsa.aws.access_key_id:access_key_id}")
    private String accessKey;

    @Value("${jsa.aws.secret_access_key:super_secret_key}")
    private String secretKey;

    @Value("${jsa.s3.region:eu-west-1}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        return s3Client;
    }

}
