package com.novare.uploadfile.upload;

import com.novare.uploadfile.exceptions.StorageException;

import com.novare.uploadfile.exceptions.StorageFileNotFoundException;
import com.novare.uploadfile.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;



@Service
public class FileSystemStorageService implements IStorageService{

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);
    
    private final Path rootLocation;

    public FileSystemStorageService(@Value("${upload.root-location}") String path) {
        this.rootLocation = Paths.get(path);
    }

    /**
     * Method that initializes the project's secondary storage
     */
    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * Method that stores a file in secondary storage from a object
     * type {@link org.springframework.web.multipart#MultipartFile} MultipartFile
     * I modified the original Spring example to change the name of the file to store.
     * As we associate it to the Content that has been registered, we will use the content ID as the file name.
     *
     */
    @Override
    public String store(MultipartFile file) {
        try {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(filename);
            String storedFilename = System.currentTimeMillis() + "." + extension;

            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(storedFilename),
                        StandardCopyOption.REPLACE_EXISTING);
                return storedFilename;
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file");
        }
    }

    /**
     * Method receives like parameter a byte array (case file as base64) and  store the file in the rootLocation.
     */
    @Override
    public String storeBase64(byte[] file) {
        try {
            InputStream inputStream = new ByteArrayInputStream(file);
            String storedFilename = FileUtil.getStorageFileName(inputStream);
            Files.copy(inputStream, this.rootLocation.resolve(storedFilename),
                    StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        }catch (IOException e){
            String errorMessage = String.format("%s: %s", "Error when storeBase64: ", e.getMessage());
            logger.error(errorMessage);
            return null;
        }
    }

    /**
     * Method that returns the path of all the files that exist in the rootLocation (inside project)
     */
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    /**
     * Method that is capable of loading a file from its name
     * Returns an object of type Path
     */
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * Method that is capable of loading a file from its name
     * return a Resource object.
     */
    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    /**
     * Method that removes all files from storage that exist in the rootLocation (inside project)
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    /**
     * Method that removes a specific file from storage that exist in the rootLocation (inside project)
     */
    @Override
    public void delete(String filename) {
        String justFilename = StringUtils.getFilename(filename);
        try {
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Error deleting a file", e);
        }
    }


}
