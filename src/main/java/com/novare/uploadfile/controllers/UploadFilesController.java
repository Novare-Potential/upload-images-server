package com.novare.uploadfile.controllers;
import com.novare.uploadfile.dtos.ResponseContentDto;
import com.novare.uploadfile.dtos.RequestContentDto;
import com.novare.uploadfile.model.Content;
import com.novare.uploadfile.repositories.ContentRepository;

import java.io.IOException;
import java.util.List;

import com.novare.uploadfile.utils.FileUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.novare.uploadfile.uploadServices.IStorageService;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;


@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST})
@RestController
public class UploadFilesController {
    private static final Logger logger = LoggerFactory.getLogger(UploadFilesController.class);

    private final ContentRepository contentRepository;
    private final IStorageService iStorageService;

    public UploadFilesController(ContentRepository contentRepository, IStorageService iStorageService) {
        this.contentRepository = contentRepository;
        this.iStorageService = iStorageService;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename, HttpServletRequest request) {
        Resource file = iStorageService.loadAsResource(filename);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    // POST - /upload
    @PostMapping(value="/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ResponseContentDto> uploadFile(@RequestBody RequestContentDto requestContentDto){
        // Start process to handle a base64 file
        String imageDataBytes = FileUtil.getImageFromBase64(requestContentDto.getImage());
        // so you get only the image bytes and then decode them:
        byte[] decodedBytes = Base64.decodeBase64(imageDataBytes);
        String image = iStorageService.storeBase64(decodedBytes);
        // End process to handle a base64 file

        String imgUrl = MvcUriComponentsBuilder.fromMethodName(UploadFilesController.class, "getFile", image, null).build().toString();

        // When get all info to store in DB
        Content content = new Content();
        content.setTitle(requestContentDto.getTitle());
        content.setImage(imgUrl);
        contentRepository.save(content);
        // Finished persistence in the contents table.

        // TODO: Improve with a mapper or converter to organize the response dto.
        // Reference: https://roytuts.com/spring-boot-data-jpa-entity-dto-mapping-using-mapstruct/
        ResponseContentDto responseContentDto = new ResponseContentDto();
        responseContentDto.setImgUrl(content.getImage());
        responseContentDto.setTitle(content.getTitle());
        // HTTP STATUS CODE - 201
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseContentDto);
    }

    @GetMapping(value = "/content")
    public ResponseEntity<List<Content>> getContent(){
        return ResponseEntity.ok().body(contentRepository.findAll());
    }

}
