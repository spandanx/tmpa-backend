package com.example.musicplayer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.musicplayer.model.CustomException;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.LoginRepository;
import com.example.musicplayer.repository.MusicRepository;
import com.example.musicplayer.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService{
	
	@Autowired
	LoginRepository loginRepository;

	@Override
	public String authenticate(String username, String password) throws CustomException {
		User user = loginRepository.findByUsername(username);
		System.out.println(user);
		if (user==null) {
			throw new CustomException("Wrong Username");
		}
		else {
			if (password.equals(user.getPassword())) {
				return user.getRole();
			}
			else {
				throw new CustomException("Wrong Credentials");
			}
		}
	}

	@Override
	public String[] getPermissions(String username) {
		return loginRepository.getPermissions(username);
	}

	@Override
	public String register(String username, String password) throws CustomException {
		System.out.println("Calling register()");
		User existingUser = loginRepository.findByUsername(username);
		if (existingUser==null) {
			User user = new User(username, password, "user");
			loginRepository.save(user);
			return "Success";
		}
		else {
			throw new CustomException("Username Already exists!");
		}
	}

}
