package com.example.musicplayer.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.example.musicplayer.model.Music;

import reactor.core.publisher.Mono;

public interface MusicFileService {
	
//	public Mono<Resource> getAudio(String title);
//	
//	public Mono<Resource> getThumbnail(String title);
//	
	List<Music> getAllMusic(String filter);

	ResponseEntity<byte[]> getContent(String fileName, String range, String contentTypePrefix);
	
	Music[] getMusicByCondition(String column, String value);
	
	void save(Music music);
	
	List<String> getLanguages();
	
	List<String> GetCategoryNames();
	
	List<String> GetCategoryValues(String language, String categoryName);

	List<Music> getCustomMusic(Map<String, String> map);
}
