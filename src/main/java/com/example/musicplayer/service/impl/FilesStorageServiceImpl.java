package com.example.musicplayer.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.musicplayer.service.FilesStorageService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

  private final Path musicPath = Paths.get(".\\src\\main\\resources\\upload\\audio");
  
  private final Path thumbnailPath = Paths.get(".\\src\\main\\resources\\upload\\thumbnail");
  
  @Autowired
  private Environment env;
  
  public static final int BYTE_RANGE = 128;

  @Override
  public void init() {
    try {
      Files.createDirectory(musicPath);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  @Override
  public void saveAudio(MultipartFile file, String name) {
    try {
//      Files.copy(file.getInputStream(), this.musicPath.resolve(file.getOriginalFilename()));
//    	Files.copy(file.getInputStream(), this.musicPath.resolve(name));
    	System.out.println("Calling saveAudio()");
		String path = env.getProperty("ftp.musicUploadDir");
		name = path+name;
		System.out.println("PATH:");
		System.out.println(path);
		System.out.println(name);
		uploadFileTest(file, name);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }
  
  @Override
  public void saveThumbnail(MultipartFile file, String name) {
	  try {
//		  Files.copy(file.getInputStream(), this.thumbnailPath.resolve(file.getOriginalFilename()));
//	    	Files.copy(file.getInputStream(), this.thumbnailPath.resolve(name));
		  	System.out.println("Calling saveAudio()");
			String path = env.getProperty("ftp.thumbnailUpoloadDir");
			name = path+name;
			System.out.println("PATH:");
			System.out.println(path);
			System.out.println(name);
			uploadFileTest(file, name);
	    } catch (Exception e) {
	      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
	    }
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = musicPath.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read the file!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(musicPath.toFile());
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.musicPath, 1).filter(path -> !path.equals(this.musicPath)).map(this.musicPath::relativize);
    } catch (IOException e) {
      throw new RuntimeException("Could not load the files!");
    }
  }
  
  
//  private DefaultSftpSessionFactory getConnection() {
//		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
////		factory.setHost("ftp.tmpa.online");
//		factory.setHost(env.getProperty("ftp.host"));
////		factory.setHost("abcdftmpa.online");
//		factory.setPort(Integer.parseInt(env.getProperty("ftp.port")));
//		factory.setAllowUnknownKeys(true);
////		factory.setUser("u741958148.music_user");
//		factory.setUser(env.getProperty("ftp.username"));
////		factory.setPassword("Yes #200");
//		factory.setPassword(env.getProperty("ftp.password"));
//		return factory;
//	}
  
  private void uploadFileTest(MultipartFile file, String destination) {
	  String server = env.getProperty("ftp.host");
      int port = Integer.parseInt(env.getProperty("ftp.port"));
      String user = env.getProperty("ftp.username");
      String pass = env.getProperty("ftp.password");
      FTPClient ftpClient = new FTPClient();
      try {
	      ftpClient.connect(server, port);
	      System.out.println("CONNECTED");
	      ftpClient.login(user, pass);
	      System.out.println("LOGGED IN");
	      ftpClient.enterLocalPassiveMode();
	      System.out.println("ENTERED IN PASSIVE MODE");
	      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	      
          InputStream inputStream =  new BufferedInputStream(file.getInputStream());

          System.out.println("Start uploading first file");
          boolean done = ftpClient.storeFile(destination, inputStream);
//          ftpClient.ge
          if (done) {
              System.out.println(file.getOriginalFilename() +" is uploaded successfully.");
          }
          System.out.println("Directories:");
	      System.out.println(Arrays.toString(ftpClient.listDirectories()));
	      System.out.println("Directories of /home/vsftpd/:");
	      System.out.println(Arrays.toString(ftpClient.listDirectories("/home/vsftpd/")));
	      System.out.println("Files:");
	      System.out.println(Arrays.toString(ftpClient.listFiles()));
	      System.out.println("Files of /home/vsftpd/:");
	      System.out.println(Arrays.toString(ftpClient.listFiles("/home/vsftpd/")));
	      System.out.println("Current directory:");
	      System.out.println(ftpClient.printWorkingDirectory());
          
          inputStream.close();
          ftpClient.logout();
          ftpClient.disconnect();
       // APPROACH #2: uploads second file using an OutputStream
//          File secondLocalFile = new File("E:/Test/Report.doc");
//          String secondRemoteFile = "test/Report.doc";
//          inputStream = new FileInputStream(secondLocalFile);
//
//          System.out.println("Start uploading second file");
//          OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
//          byte[] bytesIn = new byte[4096];
//          int read = 0;
//
//          while ((read = inputStream.read(bytesIn)) != -1) {
//              outputStream.write(bytesIn, 0, read);
//          }
//          inputStream.close();
//          outputStream.close();
//
//          boolean completed = ftpClient.completePendingCommand();
//          if (completed) {
//              System.out.println("The second file is uploaded successfully.");
//          }
      }
      catch(Exception e) {
    	  System.out.println("ERROR");
    	  System.out.println(e.getMessage());
      }
  } 
  
	@Override
	public void uploadFile(MultipartFile file, String destination) throws IOException {
		System.out.println("Calling uploadFile()");
		String path = env.getProperty("ftp.musicUploadDir");
		destination = path+destination;
		System.out.println("PATH:");
		System.out.println(path);
		System.out.println(destination);
		uploadFileTest(file, destination);
	}
	
	public byte[] readFile(String filename, String range) throws IOException { // ftp
		System.out.println("Calling readFile()");
		String server = env.getProperty("ftp.host");
		int port = Integer.parseInt(env.getProperty("ftp.port"));
		String user = env.getProperty("ftp.username");
		String pass = env.getProperty("ftp.password");
		FTPClient ftpClient = new FTPClient();
		try {
		      ftpClient.connect(server, port);
		      System.out.println("CONNECTED");
		      ftpClient.login(user, pass);
		      System.out.println("LOGGED IN");
		      ftpClient.enterLocalPassiveMode();
		      System.out.println("ENTERED IN PASSIVE MODE");
		      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		      
//		      FTPFile clientFile = ftpClient.mlistFile(filename);
		      FTPFile clientFile = ftpClient.listFiles(filename)[0];
		      System.out.println("Directories:");
		      System.out.println(Arrays.toString(ftpClient.listDirectories()));
		      System.out.println("Directories of /home/vsftpd/:");
		      System.out.println(Arrays.toString(ftpClient.listDirectories("/home/vsftpd/")));
		      System.out.println("Files:");
		      System.out.println(Arrays.toString(ftpClient.listFiles()));
		      System.out.println("Files of /home/vsftpd/:");
		      System.out.println(Arrays.toString(ftpClient.listFiles("/home/vsftpd/")));
		      System.out.println("Current directory:");
		      System.out.println(ftpClient.printWorkingDirectory());
	          long size = clientFile.getSize();
	          System.out.println("FileName:"+filename+", File size:" + size);
	          long rangeStart = 0;
	          long rangeEnd;
	          if (range == null) {
	        	  rangeEnd = size-1;
	          }
	          else {
			      String[] ranges = range.split("-");
			      rangeStart = Long.parseLong(ranges[0].substring(6));
			      if (ranges.length > 1) {
			    	  rangeEnd = Long.parseLong(ranges[1]);
			      } else {
			    	  rangeEnd = size - 1;
			      }
			      if (size < rangeEnd) {
			    	  rangeEnd = size - 1;
			      }
	          }
		      
		      InputStream inputStream = ftpClient.retrieveFileStream(filename);
		      ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
		      byte[] data = new byte[BYTE_RANGE];
		      int nRead;
		      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
		    	  bufferedOutputStream.write(data, 0, nRead);
		      }
		      byte[] result = new byte[(int) (rangeEnd - rangeStart) + 1];
		      System.arraycopy(bufferedOutputStream.toByteArray(), (int) rangeStart, result, 0, result.length);
		      bufferedOutputStream.flush();
	          inputStream.close();
	          ftpClient.logout();
	          ftpClient.disconnect();
	          
	          return result;
	     }
	     catch(Exception e) {
	    	  System.out.println("ERROR");
	    	  System.out.println(e.getMessage());
	     }
		return null;
	}

	@Override
	public byte[] readAudio(String fileName, String range) throws IOException {
		String path = env.getProperty("ftp.musicUploadDir");
		return readFile(path+fileName, range);
	}

	@Override
	public byte[] readThumbnail(String fileName, String range) throws IOException {
		String path = env.getProperty("ftp.thumbnailUpoloadDir");
		return readFile(path+fileName, range);
	}
}