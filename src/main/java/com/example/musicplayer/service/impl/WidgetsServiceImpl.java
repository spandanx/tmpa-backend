package com.example.musicplayer.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.musicplayer.model.Widgets;
import com.example.musicplayer.repository.WidgetsRepository;
import com.example.musicplayer.service.WidgetsService;

@Service
public class WidgetsServiceImpl implements WidgetsService{
	
	@Autowired
	WidgetsRepository dashBoardRepository;

	@Override
	public List<Widgets> getDashBoard(int dashBoardId) {
		return (List<Widgets>) dashBoardRepository.findAll();
	}

}
