package com.example.musicplayer.repository;

import java.util.List;

import org.json.JSONObject;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.musicplayer.model.Music;

@Repository
//public interface MusicRepository extends ElasticsearchRepository<Music, String>{
public interface MusicRepository extends CrudRepository<Music, String>{
	
//	@Query(value = "?1", nativeQuery = true)
	
	@Query(value = "SELECT * FROM MUSIC WHERE NAME = ?1", nativeQuery = true)
	Music[] findByName(String value);
	
	@Query(value = "SELECT * FROM MUSIC WHERE CATEGORY = ?1", nativeQuery = true)
	Music[] findByCategory(String value);
	
	@Query(value = "SELECT * FROM MUSIC WHERE ARTISTS = ?1", nativeQuery = true)
	Music[] findByArtists(String value);
	
	@Query(value = "SELECT DISTINCT(language) from music", nativeQuery = true)
	List<String> getLanguages();
	
	@Query(value = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'music'", nativeQuery = true)
	List<String> getCategoryNames();
	
	@Query(value = "SELECT DISTINCT(?2) from music WHERE language = ?1", nativeQuery = true)
	List<String> getCategoryValue(String language, String categoryName);
	
}
