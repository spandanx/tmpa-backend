package com.example.musicplayer.service;

import com.example.musicplayer.model.CustomException;

public interface LoginService {
	
	String authenticate(String username, String password) throws CustomException;
	
	String[] getPermissions(String username);
	
	String register(String username, String password) throws CustomException;
}
