package com.example.musicplayer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "widgets")
@Getter
@Setter
public class Widgets {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "widget_id")
	int id;
	
	@Column(name = "dashboard_id")
	int dashboardId;
	
	@Column(name = "widget_name")
	String widgetName;
	
	@Column//(name = "order_id")
	int order_id;
	
	@Column(name = "section_id")
	int sectionId;
	
	@Column(name = "query")
	String query;
}
