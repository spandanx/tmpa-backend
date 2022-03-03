package com.example.musicplayer.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3StorageService {
	
	
	public void uploadAudio(MultipartFile file, String fileName);
	
	public void uploadThumbnail(MultipartFile file, String fileName);
    
    public Object[] readAudio(String fileName, String range) throws IOException;
    
    public byte[] readThumbnail(String fileName, String range) throws IOException;

//    public String deleteFile(String fileName);
}