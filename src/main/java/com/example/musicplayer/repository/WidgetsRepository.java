package com.example.musicplayer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.musicplayer.model.Widgets;

@Repository
public interface WidgetsRepository extends CrudRepository<Widgets, Integer>{
	
	@Query(value = "SELECT w from Widgets w where w.dashboardId=:dashboardId")
	Widgets getWidgetsById(@Param("dashboardId") String dashboardId);
}
