package com.example.musicplayer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.musicplayer.model.User;

//##################################################################### Annotation
public interface LoginRepository extends CrudRepository<User, String> {

	@Query(value = "SELECT * FROM USER WHERE username = ?1", nativeQuery = true)
	User findByUsername(String username);
	
	@Query(value = "SELECT access_name FROM permission_map where username = ?1", nativeQuery = true)
	String[] getPermissions(String username);
	
}
