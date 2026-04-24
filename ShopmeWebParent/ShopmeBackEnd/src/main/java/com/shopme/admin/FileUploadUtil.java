package com.shopme.admin;

import java.io.*;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
    public static void saveFile(String uploadDir, String fileName, 
            MultipartFile multipartFile) throws IOException {
        
        Path uploadPath = Paths.get(uploadDir);

        //Kon wala pa ang folder, himuon nato kini
        if (!Files.exists(uploadPath)) {
        	
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            
            //I-copy ang file ug i-overwrite kon naa nay karaan
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }
    }
    
    // This testing kung mausab ba ang icon sa mga folder.
    // limpyohan nya ang sulod sa folder sa usa ka user aron usa nalang ka image ang magpabilin.
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);

		if (Files.exists(dirPath)) {
			try {
				Files.list(dirPath).forEach(file -> {
					if (!Files.isDirectory(file)) {
						try {
							Files.delete(file);
						} catch (IOException ex) {
							LOGGER.error("Could not delete file: " + file);
							//System.out.println("Could not delete file: " + file);
						}
					}
				});
			} catch (IOException ex) {
				LOGGER.error("Could not list directory: " + dirPath);
				//System.out.println("Could not list directory: " + dirPath);
			}
		}
	}
    
    // iyaha i-remove ang tibuok folder sa image during sa pag delete sa file sa users.
    public static void removeDir(String dir) {
    	cleanDir(dir); // limpyohan una ang sulod sa (files)
    	
    	try {
            Files.delete(Paths.get(dir)); // Human, tangtangon ang folder
        } catch (IOException e) {
            System.out.println("Could not remove directory: " + dir);
        }
    }
}