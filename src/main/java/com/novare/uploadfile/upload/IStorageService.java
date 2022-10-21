package com.novare.uploadfile.upload;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 /**
 * This interface allows us to define an abstraction of what should
 * be a secondary store of information, so that we can use it
 * on a controller.
 *
 * In this way, we will be able to use a store that accesses our
 * file system, or we could also implement another one that was
 * on a remote system, store the files on a GridFS system, ...
 *
 */

public interface IStorageService {

    void init();

    String store(MultipartFile file);
    String storeBase64(byte[] file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void delete(String filename);
}
