package com.example.musicplayer.web.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.musicplayer.model.CustomException;
import com.example.musicplayer.model.FileInfo;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.model.ResponseMessage;
import com.example.musicplayer.model.Widgets;
import com.example.musicplayer.repository.MusicRepository;
import com.example.musicplayer.service.WidgetsService;
import com.example.musicplayer.service.FilesStorageService;
import com.example.musicplayer.service.LoginService;
import com.example.musicplayer.service.MusicFileService;
import com.example.musicplayer.service.S3StorageService;
import com.example.musicplayer.service.impl.LoginServiceImpl;
import com.example.musicplayer.service.impl.MusicFileServiceImpl;

import reactor.core.publisher.Mono;


//@PropertySource("classpath:applciation.properties") 
//@ConfigurationProperties
//@ConfigurationProperties
//@PropertySource("classpath:application.properties")

@Controller
@CrossOrigin("http://localhost:4200")
@RequestMapping(value = "/api")
public class MusicController {
	
	@Autowired
	private MusicFileService musicFileService;
	
	@Autowired
	private LoginService loginService;
		
	@Autowired
	private FilesStorageService storageService;
	
	@Autowired
	private S3StorageService s3StorageService;
	
	@Autowired
	private WidgetsService dashBoardService;
	
	@Autowired
	private Environment env;
	
	private final String USERNAME = "username";
	
	private final String PASSWORD = "password";
	
	
//	@GetMapping(value = "/a/{title}")//"audio/mp3"//, produces = "audio/mp3"
//	public Mono<Resource> getAudio(@PathVariable String title, @RequestHeader("Range") String range) {
////	public ResponseEntity<String> getAudio(@PathVariable String title, @RequestHeader("Range") String range) {
////        System.out.println("range in bytes() : " + range);
//		System.out.println("Calling getAudio()");
//		return musicFileServiceImpl.getAudio(title);
////        return new ResponseEntity<Mono<Resource>> (musicFileServiceImpl.getAudio(title), HttpStatus.OK);
////		return ResponseEntity.status(HttpStatus.OK).body("Sample");
//    }
//	@GetMapping(value = "video/{title}", produces = "audio/mp3")
//	public Mono<Resource> getVideos(@PathVariable String title, @RequestHeader("Range") String range) {
//		System.out.println("Calling getVideos()");
//        return musicFileServiceImpl.getVideo(title);
//	}
//	
//	@GetMapping(value = "audio/{title}", produces = "audio/mp3")
//	public Mono<Resource> getAudio(@PathVariable String title, @RequestHeader("Range") String range) {
//
//		System.out.println("Calling getAudio()");
//        return musicFileServiceImpl.getAudio(title);
//	}
//	
//	@GetMapping(value = "/thumbnail/{title}", produces = "image/*")
//	public Mono<Resource> getThumnail(@PathVariable String title, @RequestHeader("Range") String range) {
//		System.out.println("Calling getThumnail()");
//        return musicFileServiceImpl.getThumbnail(title);
//    }
	
	@GetMapping(value = "/get-all-music")
	public ResponseEntity<List<Music>> getAllMusic(){
		System.out.println("Calling getAllMusic()");
		List<Music> musicList = musicFileService.getAllMusic(null);
		System.out.println(musicList);
//		return (ResponseEntity<List<Music>>) musicList;
		return new ResponseEntity<List<Music>>(musicList, HttpStatus.OK);
	}
//	@PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
//        return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
//    }
//
	
//	@PostMapping("/upload-audio")
//	  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
//	    System.out.println("Calling uploadFile() Controller");
//		String message = "";
//	    try {
////	    	System.out.println(file.getContentType());
////	    	System.out.println(file.getName());
////	    	System.out.println(file.getOriginalFilename());
////	    	System.out.println(file.getSize());
	
////	    	System.out.println(file.getContentType());
//	      storageService.saveAudio(file, name); 
//

//	      message = "Uploaded the audio file successfully: " + file.getOriginalFilename();
//	      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
//	    } catch (Exception e) {
//	      message = "Could not upload the audio file: " + file.getOriginalFilename() + "!";
//	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
//	    }
//	  }
	@PostMapping("/upload-audio")
	  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
	    System.out.println("Calling uploadFile() Controller");
		String message = "";
	    try {
//	    	System.out.println(file.getContentType());
//	    	System.out.println(file.getName());
	    	
//	    	System.out.println(file.getOriginalFilename());s
//	    	System.out.println(file.getSize());
	    	
//	    	System.out.println(file.getContentType());
	      s3StorageService.uploadAudio(file, name);

	      message = "Uploaded the audio file successfully: " + file.getOriginalFilename();
	      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
	    } catch (Exception e) {
	      message = "Could not upload the audio file: " + file.getOriginalFilename() + "!";
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
	    }
	  }
	
//	@PostMapping("/upload-audio2")
//	  public ResponseEntity<ResponseMessage> uploadFile2(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
//	    System.out.println("Calling uploadFile2() Controller");
//		String message = "";
//	    try {
////	    	System.out.println(file.getContentType());
////	    	System.out.println(file.getName());
////	    	System.out.println(file.getOriginalFilename());
////	    	System.out.println(file.getSize());
////	    	System.out.println(file.getContentType());
//	    	storageService.uploadFile(file, name);
//	    	message = "Uploaded the audio file successfully: " + file.getOriginalFilename();
//	    	return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
//	    } catch (Exception e) { 
//	      message = "Could not upload the audio file: " + file.getOriginalFilename() + "!";
//	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
//	    }
//	  }
	
	@PostMapping("/upload-thumbnail")
	  public ResponseEntity<ResponseMessage> uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
	    System.out.println("Calling uploadThumbnail() Controller");
		String message = "";
	    try {
//	    	System.out.println(file.getContentType());
//	    	System.out.println(file.getName());
//	    	System.out.println(file.getOriginalFilename());
//	    	System.out.println(file.getSize()); 
//	    	System.out.println(file.getContentType());
//	      storageService.saveThumbnail(file, name);
	    	
	    	s3StorageService.uploadThumbnail(file, name);

	      message = "Uploaded the thumbnail file successfully: " + file.getOriginalFilename();
	      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
	    } catch (Exception e) {
	      message = "Could not upload the thumbnail file: " + file.getOriginalFilename() + "!";
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
	    }
	  }
	
	@PostMapping("/upload-details")
	public ResponseEntity<String> saveToElastic(@RequestBody Music music){
		System.out.println("Calling saveToElastic()");
		try {
			
//			String audioFilePath = music.getAudioFilePath();
			
//			System.out.println("Got audioPath");
			
			
			
			
//			System.out.println(music.toString());
			
			
			
			
//			Long epocValue = Long.valueOf(music.getUploadDate().to);
//			System.out.println("Got epocValue:"+epocValue);
//			LocalDateTime uploadDate =  LocalDateTime.ofEpochSecond(epocValue, 0, ZoneOffset.UTC);
//			System.out.println("Converted to localdatetime");
//			music.setUploadDate(uploadDate);
			musicFileService.save(music);
			System.out.println(music.toString());
			return ResponseEntity.status(HttpStatus.OK).body("Uploaded details");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Problem in upload");
	}

	  @GetMapping("/files")
	  public ResponseEntity<List<FileInfo>> getListFiles() {
	    List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
	      String filename = path.getFileName().toString();
//	      String url = MvcUriComponentsBuilder.fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
	      String url = filename;
	      return new FileInfo(filename, url);
	    }).collect(Collectors.toList());

	    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	  }

	  @GetMapping("/files/{filename:.+}")
	  @ResponseBody
	  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
	    Resource file = storageService.load(filename);
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
	  }
	  //#################################################
//    @GetMapping("/download/{fileName}")
//    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
//        byte[] data = service.downloadFile(fileName);
//        ByteArrayResource resource = new ByteArrayResource(data);
//        return ResponseEntity
//                .ok()
//                .contentLength(data.length)
//                .header("Content-type", "application/octet-stream")
//                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
//                .body(resource);
//    }
	  
	  @GetMapping(value="/audio/{fileName}", produces="application/octet-stream")
	  public Mono<ResponseEntity<byte[]>> getAudio(@RequestHeader(value = "Range", required = false) String httpRangeList,
	                                      @PathVariable("fileName") String fileName) {
//	     return Mono.just(musicFileService.getContent(fileName, httpRangeList, "audio"));
		  try {
//			  long[] ranges = s3StorageService.getRange(fileName, httpRangeList);
//			  System.out.println("Content Range: "+contentRange);
			  //.header("Content-Range", contentRange)
			
			  
			  
			  Object[] obj = s3StorageService.readAudio(fileName, httpRangeList);
			  byte[] data = (byte[]) obj[0];
			  long rangeStart = (long) obj[1];
			  long rangeEnd = (long) obj[2];
			  long size = (long) obj[3];
//			  System.out.println("In object Outsize: Start="+obj[1]+", end="+obj[2]+", size"+obj[3]);
			  String contentRange = "bytes "+ rangeStart+"-"+rangeEnd+"/"+size;
//			  System.out.println(contentRange);
			  
			  return Mono.just(ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).header("Content-Range", contentRange).body(data));
		  } catch (IOException e) {
			  return Mono.just(ResponseEntity.status(HttpStatus.OK).body(null));
		  }
	  }
//	  @GetMapping("/audio/{fileName}")
//	  public Mono<ResponseEntity<byte[]>> getAudio(@RequestHeader(value = "Range", required = false) String httpRangeList,
//	                                      @PathVariable("fileName") String fileName) {
////	     return Mono.just(musicFileService.getContent(fileName, httpRangeList, "audio"));
//		  try {
//			  return Mono.just(ResponseEntity.status(HttpStatus.OK).body(storageService.readAudio(fileName, httpRangeList)));
//		  } catch (IOException e) {
//			  return Mono.just(ResponseEntity.status(HttpStatus.OK).body(null));
//		  }
//	  }
	  
	  @GetMapping("/thumbnail/{fileName}")
	  public Mono<ResponseEntity<byte[]>> getThumbnail(@RequestHeader(value = "Range", required = false) String httpRangeList,
	                                      @PathVariable("fileName") String fileName) {
		  try {
//			  return Mono.just(ResponseEntity.status(HttpStatus.OK).body(storageService.readThumbnail(fileName, httpRangeList)));
			  return Mono.just(ResponseEntity.status(HttpStatus.OK).body(s3StorageService.readThumbnail(fileName, httpRangeList)));
		  } catch (IOException e) {
			  return Mono.just(ResponseEntity.status(HttpStatus.OK).body(null));
		  }
	  }
	  
	  @PostMapping("/authenticate")
	  public ResponseEntity<String> authenticate(@RequestBody Map<String, String> credential){
//		  System.out.println("Calling authenticate()");
//		  System.out.println(credential.toString());
//		  JSONObject credential = (JSONObject) data;
		  try {
			String role = loginService.authenticate(credential.get(USERNAME), credential.get(PASSWORD));
			String[] permissions = loginService.getPermissions(credential.get(USERNAME));
			JSONObject json = new JSONObject();
			json.put("role", role);
			json.put("permissions", permissions);
			return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		} catch (CustomException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		}
	  }
	  
	  @PostMapping("/query")
	  public ResponseEntity<Music[]> executeQuery(@RequestBody Map<String, String> queryData){
		  String query = queryData.get("query");
		  System.out.println(query);
		  int i= 0;
		  String key = "";
		  String value = "";
		  for (Entry<String, String> element: queryData.entrySet()) {
			  if (i==1)
				  break;
			  key = element.getKey();
			  value = element.getValue();
			  i++;
		  }
		  Music[] musicArr = musicFileService.getMusicByCondition(key, value);
		  System.out.println(Arrays.toString(musicArr));
		  return ResponseEntity.status(HttpStatus.OK).body(musicArr);
	  }
	  
	  @PostMapping("/register")
	  public ResponseEntity<String> register(@RequestBody Map<String, String> credential){
		  System.out.println("Calling authenticate()");
//		  System.out.println(credential);
		  try {
			  loginService.register(credential.get(USERNAME), credential.get(PASSWORD));
			  JSONObject json = new JSONObject();
			  json.put("message", "Successfully created user");
			  return ResponseEntity.status(HttpStatus.OK).body(json.toString());
		  }
		  catch (CustomException e) {
			  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		  }
	  }
	  @GetMapping("/filecheck")
	  public ResponseEntity<String> filecheck(@RequestParam("path") String filepath, @RequestParam("type") String type){
		  System.out.println("PATH");
		  System.out.println(filepath);
//		  String path = env.getProperty("file");
		  
		  File directoryPath = new File(filepath);
	      //List of all files and directories
	      String contents[] = directoryPath.list();
	      
	      Path currentRelativePath = Paths.get("");
	      String currentPath = currentRelativePath.toAbsolutePath().toString();
//	      System.out.println("List of files and directories in the specified directory:");
//	      for(int i=0; i<contents.length; i++) {
//	         System.out.println(contents[i]);
//	      }
	      
//	      System.out.println(Arrays.toString(contents));
////		  System.out.println(credential);
//		  String path = ""; 
//		  String file = "";
//		  try {
//			path = Files.createTempDirectory("").toString() + File.separator;
//			
//			File abc = File.createTempFile("cde", ".txt");
//			file = abc.getPath().toString();
//			System.out.println(file);
//			System.out.println(abc.getAbsolutePath().toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
//		}
	      if (type.equals("find")) {
	    	  return ResponseEntity.status(HttpStatus.OK).body(currentPath);
	      }
	      if (contents==null)
	    	  return ResponseEntity.status(HttpStatus.OK).body("NULL");
	      else
	    	  return ResponseEntity.status(HttpStatus.OK).body(Arrays.toString(contents));
	  }
	  
	  @GetMapping("distinct-language")
	  public ResponseEntity<List<String>> getLanguages(){
		  return ResponseEntity.status(HttpStatus.OK).body(musicFileService.getLanguages());
	  }
	  
	  @GetMapping("categoryNames")
	  public ResponseEntity<List<String>> getCategoryNames(){
		  return ResponseEntity.status(HttpStatus.OK).body(musicFileService.GetCategoryNames());
	  }
	  
	  @GetMapping("categoryValues/{language}/{categoryName}")
	  public ResponseEntity<List<String>> getCategoryNames(@PathVariable(value = "language") String language, @PathVariable(value = "categoryName") String categoryName){
		  return ResponseEntity.status(HttpStatus.OK).body(musicFileService.GetCategoryValues(language, categoryName));
	  }
	  
	  @PostMapping("customQuery")
	  public ResponseEntity<List<Music>> getCustomQuery(@RequestBody Map<String, String> map){
		  System.out.println("Inside getCustomQuery()");
		  System.out.println(map);
		  List<Music> musics = musicFileService.getCustomMusic(map);
//		  return ResponseEntity.status(HttpStatus.OK).body(musics);
		  return new ResponseEntity<List<Music>>(musics, HttpStatus.OK);
	  }
	  
	  @GetMapping("getDashBoard/{id}") 
	  public ResponseEntity<List<Widgets>> getDashBoard(@PathVariable("id") int dashboardId){
		  return ResponseEntity.status(HttpStatus.OK).body(dashBoardService.getDashBoard(dashboardId));
	  } 
}