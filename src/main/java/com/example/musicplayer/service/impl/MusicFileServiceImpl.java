package com.example.musicplayer.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.example.musicplayer.model.Music;
import com.example.musicplayer.repository.MusicRepository;
import com.example.musicplayer.service.MusicFileService;

import reactor.core.publisher.Mono;

@Service
public class MusicFileServiceImpl implements MusicFileService{

//	private static final String AUDIOPATH="classpath:/upload/audio/%s.mp3";
//	private static final String AUDIOPATH="classpath:upload/audio/%s";
//	private static final String AUDIOPATH="classpath:%s";
	
	public static final String AUDIO_PATH = "/upload/audio";
	public static final String THUMBNAIL_PATH = "/upload/thumbnail";
	
	public static final int BYTE_RANGE = 128;
	
	private static final String THUMBNAILPATH="classpath:upload/audio/%s.mp3";
	
	private static final String FORMAT="classpath:/%s.mp3";
	
//	@Autowireds
//    private ResourceLoader resourceLoader;
	
	@Autowired
	private MusicRepository musicRepository;
	
	@Autowired
	EntityManager entityManager;
	
	 
	
//    private final Path musicPath = Paths.get("./src/main/resources/upload/");
	
//	public Mono<Resource> getVideo(String title) {
//		try {
//			System.out.println(resourceLoader.
//			         getResource(String.format(FORMAT,title)).contentLength());
//			System.out.println("Got file from getVideo()");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Problem in getVideo()");
//		}
//		return Mono.fromSupplier(()->resourceLoader.
//	             getResource(String.format(FORMAT,title)));
//	}


//	@Override
//	public Mono<Resource> getAudio(String title) {
//		System.out.println(String.format(AUDIOPATH,title));
//		System.out.println("FILE");
//		System.out.println(resourceLoader.
//	             getResource(String.format(AUDIOPATH,title)).isFile());
//		try {
//			System.out.println(resourceLoader.
//			         getResource(String.format(AUDIOPATH,title)).contentLength());
//			System.out.println("FILE FOUND");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("FILE NOT FOUND");
//		}
//		return Mono.fromSupplier(()->resourceLoader.
//	             getResource(String.format(AUDIOPATH,title)));
//	}
//
//	@Override
//	public Mono<Resource> getThumbnail(String title) {
//		return Mono.fromSupplier(()->resourceLoader.
//	             getResource(String.format(THUMBNAILPATH,title)));
//	}

	@Override
	public List<Music> getAllMusic(String filter) {
		// TODO Auto-generated method stub
		List<Music> musicList = new ArrayList<>();
		Iterable<Music>  musicIterable = musicRepository.findAll();
		for (Music music: musicIterable) {
			musicList.add(music);
		}
		return musicList;
	}

	
//	@Override
//	public List<Music> getAllMusic(String filter) {
//		List<Music> musicList = new ArrayList<>();
//		Iterable<Music>  musicIterable = musicRepository.findAll();
//		for (Music music: musicIterable) {
//			musicList.add(music);
//		}
//		return musicList;
//	}
	
	//###############################
	@Override
	public ResponseEntity<byte[]> getContent(String fileName, String range, String contentTypePrefix) {
		String location = contentTypePrefix.equals("audio")? AUDIO_PATH:THUMBNAIL_PATH;
	     long rangeStart = 0;
	     long rangeEnd;
	     byte[] data;
	     Long fileSize;
	     String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
	     try {
	        fileSize = Optional.ofNullable(fileName)
	              .map(file -> Paths.get(getFilePath(location), file))
	              .map(this::sizeFromFile)
	              .orElse(0L);
	        if (range == null) {
	           return ResponseEntity.status(HttpStatus.OK)
	                 .header("Content-Type", contentTypePrefix+"/" + fileType)
	                 .header("Content-Length", String.valueOf(fileSize))
	                 .body(readByteRange(location, fileName, rangeStart, fileSize - 1));
	        }
	        String[] ranges = range.split("-");
	        rangeStart = Long.parseLong(ranges[0].substring(6));
	        if (ranges.length > 1) {
	           rangeEnd = Long.parseLong(ranges[1]);
	        } else {
	           rangeEnd = fileSize - 1;
	        }
	        if (fileSize < rangeEnd) {
	           rangeEnd = fileSize - 1;
	        }
	        data = readByteRange(location, fileName, rangeStart, rangeEnd);
	     } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	     String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
	     return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
	           .header("Content-Type", contentTypePrefix+"/" + fileType)
	           .header("Accept-Ranges", "bytes")
	           .header("Content-Length", contentLength)
	           .header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
	           .body(data);
	  }
	  public byte[] readByteRange(String location, String filename, long start, long end) throws IOException {
	     Path path = Paths.get(getFilePath(location), filename);
	     try (InputStream inputStream = (Files.newInputStream(path));
	         ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
	        byte[] data = new byte[BYTE_RANGE];
	        int nRead;
	        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
	           bufferedOutputStream.write(data, 0, nRead);
	        }
	        bufferedOutputStream.flush();
	        byte[] result = new byte[(int) (end - start) + 1];
	        System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
	        return result;
	     }
	  }
	  private String getFilePath(String location) {
	     URL url = this.getClass().getResource(location);
	     return new File(url.getFile()).getAbsolutePath();
	  }
	  private Long sizeFromFile(Path path) {
	     try {
	        return Files.size(path);
	     } catch (IOException ex) {
	        ex.printStackTrace();
	     }
	     return 0L;
	  }


	@Override
	public Music[] getMusicByCondition(String column, String value) {
		Music[] musicList;
		if (column.toLowerCase().equals("name")) {
			musicList = musicRepository.findByName(value);
		}
		else if (column.toLowerCase().equals("category")) {
			musicList = musicRepository.findByCategory(value);
		}
		else {//artists
			musicList = musicRepository.findByCategory(value);
		}
		return musicList;
	}


	@Override
	public void save(Music music) {
		musicRepository.save(music);		
	}


	@Override
	public List<String> getLanguages() {
		return musicRepository.getLanguages();
	}


	@Override
	public List<String> GetCategoryNames() {
		return musicRepository.getCategoryNames();
//		List<String> names = new ArrayList<>();
//		for (JSONObject json: jsons) {
//			names.add((String) json.get("Field"));
//		}
//		return names;
	}


	@Override
	public List<String> GetCategoryValues(String language, String categoryName) {
		return musicRepository.getCategoryValue(language, categoryName);
	}
	
	@Override
	public List<Music> getCustomMusic(Map<String, String> map){
		System.out.println("Calling getCustomMusic()");
		System.out.println(map);
		String queryString = "SELECT * FROM music";
		if (map.size()>0)
			queryString+=" WHERE ";
		for (Map.Entry<String, String> element: map.entrySet()) {
			queryString+=" "+element.getKey()+"=\'"+element.getValue()+"\' AND";
		}
		if (map.size()>0) {
			queryString = queryString.substring(0, queryString.length()-3);
		}
		System.out.println("QUERY:");
		System.out.println(queryString); 
		Query q = entityManager.createNativeQuery(queryString, Music.class);
//		Query q = entityManager.cre
//		List<Music> musics = q.getResultList();
		List<Music> musicList = new ArrayList<>();
		List<Object> musics = q.getResultList();
		for (Object music: musics) {
			musicList.add((Music) music);
		}
		return musicList;
	}
}
