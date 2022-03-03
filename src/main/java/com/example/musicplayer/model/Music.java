package com.example.musicplayer.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

//import org.springframework.data.elasticsearch.annotations.DateFormat;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Getter
@Setter
@Table(name = "music")
public class Music {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@Column
	String name;
	
	//language
	
	@Column
	String artists;
	
	@Column
	String language;
	
	@Column
    String category;
	
	@Column
    float length;
	
	@Column
    int views;
	
	@Column
    int likes;
	
	@Column
    int dislikes;
	
	@Column(name = "upload_date")
	@Temporal(TemporalType.TIMESTAMP)
    Date uploadDate;
	
	@Column(name="audio_file_path")
    String audioFilePath;
	
	@Column(name = "image_file_path")
    String imageFilePath;
    
	@Override
	public String toString() {
		return "Music [id=" + id + ", name=" + name + ", artists=" + artists + ", category=" + category + ", length="
				+ length + ", views=" + views + ", likes=" + likes + ", dislikes=" + dislikes + ", uploadDate="
				+ uploadDate + ", audioFilePath=" + audioFilePath + ", imageFilePath=" + imageFilePath + "]";
	}
}