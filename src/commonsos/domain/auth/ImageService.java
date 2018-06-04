package commonsos.domain.auth;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import commonsos.Configuration;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.UUID;

@Singleton
@Slf4j
public class ImageService {

  static final String BUCKET_NAME = "commonsos-app";

  AWSCredentials credentials;
  AmazonS3 s3client;

  @Inject Configuration config;

  @Inject void init() {
    credentials = new BasicAWSCredentials(config.awsAccessKey(), config.awsSecurityKey());
    s3client = AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion(Regions.US_EAST_2)
      .build();
  }

  public String upload(InputStream inputStream) {
    String filename = UUID.randomUUID().toString();
    metadata();
    s3client.putObject(new PutObjectRequest(BUCKET_NAME, filename, inputStream, null)
      .withMetadata(metadata())
      .withCannedAcl(CannedAccessControlList.PublicRead));
    return "https://s3-us-east-2.amazonaws.com/" + BUCKET_NAME + "/" + filename;
  }

  private ObjectMetadata metadata() {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType("image");
    return objectMetadata;
  }

  private void delete(String url) {
    s3client.deleteObject(BUCKET_NAME, url.substring(url.lastIndexOf('/') + 1));
  }
}
