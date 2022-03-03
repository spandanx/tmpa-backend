package com.example.musicplayer.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
	
	public void init();

	public void saveAudio(MultipartFile file, String name);
	
	public void saveThumbnail(MultipartFile file, String name);

	public Resource load(String filename);

	public void deleteAll();

	public Stream<Path> loadAll();

	void uploadFile(MultipartFile file, String destination) throws IOException;
	
	public byte[] readAudio(String fileName, String range) throws IOException;
	
	public byte[] readThumbnail(String fileName, String range) throws IOException;
}
