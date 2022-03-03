package com.example.musicplayer.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.musicplayer.service.S3StorageService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class S3StorageServiceImpl implements S3StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;
    
    @Autowired
    private Environment env;
    
    public static final int BYTE_RANGE = 128;
    
    private void uploadFile(MultipartFile file, String fileName) {
        File fileObj = convertMultiPartFileToFile(file);
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
    }
    
	@Override
	public void uploadAudio(MultipartFile file, String fileName) {
		String path = env.getProperty("application.musicPath");
		uploadFile(file, path+fileName);
	}

	@Override
	public void uploadThumbnail(MultipartFile file, String fileName) {
		String path = env.getProperty("application.thumbNailPath");
		uploadFile(file, path+fileName);
	}
	
	private Object[] downloadFile(String fileName, String range) throws IOException {
    	GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucketName, fileName);
    	ObjectMetadata objectMetadata = s3Client.getObjectMetadata(metadataRequest);
//    	long contentLength = objectMetadata.getContentLength();
    	long size = objectMetadata.getContentLength();
    	System.out.println("Size: "+size);
		long rangeStart = 0;
		long rangeEnd;
		if (range == null) {
			  rangeEnd = size-1;
		}
		else {
		    String[] ranges = range.split("-");
		    rangeStart = Long.parseLong(ranges[0].substring(6));
		    if (ranges.length > 1) {
		  	  rangeEnd = Long.parseLong(ranges[1]);
		    } else {
		  	  rangeEnd = size - 1;
		    }
		    if (size < rangeEnd) {
		  	  rangeEnd = size - 1;
		    }
		}
//        S3Object s3Object = s3Client.getObject(bucketName, fileName);
//        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, fileName).withRange(rangeStart, rangeEnd);
        S3ObjectInputStream inputStream = s3Client.getObject(objectRequest).getObjectContent();
        
        Object[] objArray = new Object[4];
        objArray[1] = rangeStart;
        objArray[2] = rangeEnd;
        objArray[3] = size;
//        System.out.println("Start="+rangeStart+", end="+rangeEnd+", size"+size);
//        System.out.println("In object: Start="+objArray[1]+", end="+objArray[2]+", size"+objArray[3]);
        try {
			byte[] content = IOUtils.toByteArray(inputStream);
//			byte[] result = new byte[(int) (rangeEnd - rangeStart) + 1];
//			byte[] data = new byte[BYTE_RANGE];
//			ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
//			int nRead;
//			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//				  bufferedOutputStream.write(data, 0, nRead);
//			}
//			bufferedOutputStream.flush();
////			System.arraycopy(bufferedOutputStream.toByteArray(), (int) rangeStart, result, 0, result.length);
//			System.arraycopy(bufferedOutputStream.toByteArray(), (int) rangeStart, result, (int)rangeEnd, result.length);
//			inputStream.close();
////			bufferedOutputStream.close();
//            return result;
			objArray[0] = content;
			return objArray;
        } catch (IOException e) {
        	System.out.println("ERROR at download file()");
        	System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
//    private byte[] downloadFile(String fileName, String range) throws IOException {
//    	GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucketName, fileName);
//    	ObjectMetadata objectMetadata = s3Client.getObjectMetadata(metadataRequest);
////    	long contentLength = objectMetadata.getContentLength();
//    	long size = objectMetadata.getContentLength();
//    	System.out.println("Size: "+size);
//		long rangeStart = 0;
//		long rangeEnd;
//		if (range == null) {
//			  rangeEnd = size-1;
//		}
//		else {
//		    String[] ranges = range.split("-");
//		    rangeStart = Long.parseLong(ranges[0].substring(6));
//		    if (ranges.length > 1) {
//		  	  rangeEnd = Long.parseLong(ranges[1]);
//		    } else {
//		  	  rangeEnd = size - 1;
//		    }
//		    if (size < rangeEnd) {
//		  	  rangeEnd = size - 1;
//		    }
//		}
////        S3Object s3Object = s3Client.getObject(bucketName, fileName);
////        S3ObjectInputStream inputStream = s3Object.getObjectContent();
//        GetObjectRequest objectRequest = new GetObjectRequest(bucketName, fileName).withRange(rangeStart, rangeEnd);
//        S3ObjectInputStream inputStream = s3Client.getObject(objectRequest).getObjectContent();
//        
//        try {
//			byte[] content = IOUtils.toByteArray(inputStream);
////			byte[] result = new byte[(int) (rangeEnd - rangeStart) + 1];
////			byte[] data = new byte[BYTE_RANGE];
////			ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
////			int nRead;
////			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
////				  bufferedOutputStream.write(data, 0, nRead);
////			}
////			bufferedOutputStream.flush();
//////			System.arraycopy(bufferedOutputStream.toByteArray(), (int) rangeStart, result, 0, result.length);
////			System.arraycopy(bufferedOutputStream.toByteArray(), (int) rangeStart, result, (int)rangeEnd, result.length);
////			inputStream.close();
//////			bufferedOutputStream.close();
////            return result;
//			return content;
//        } catch (IOException e) {
//        	System.out.println("ERROR at download file()");
//        	System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }
    @Override
	public Object[] readAudio(String fileName, String range) throws IOException {
		String path = env.getProperty("application.musicPath");
		return downloadFile(path+fileName, range);
	}

	@Override
	public byte[] readThumbnail(String fileName, String range) throws IOException {
		String path = env.getProperty("application.thumbNailPath");
		return (byte[]) downloadFile(path+fileName, range)[0];
	}
    
    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}