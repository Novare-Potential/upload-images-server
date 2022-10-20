package com.novare.uploadfile.controllers;
import com.novare.uploadfile.dtos.ContentDto;
import com.novare.uploadfile.dtos.RequestContentDto;
import com.novare.uploadfile.model.Content;
import com.novare.uploadfile.repositories.ContentRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.novare.uploadfile.upload.IStorageService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


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
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = (Resource) iStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping(value="/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<ContentDto> serveFile(
            @RequestPart("title") String title,
            @RequestPart(value = "file", required = true) MultipartFile file) {

        String imgUrl = null;
        ContentDto contentDto = new ContentDto();
        try {
            if (!file.isEmpty()){
                byte[] test = Base64.encodeBase64(file.getBytes());
                System.out.println(test);

                String image = iStorageService.store(file);
                imgUrl = MvcUriComponentsBuilder.fromMethodName(UploadFilesController.class, "getFile", image).build().toString();
                System.out.println(imgUrl);
            }
        } catch (Exception ex) {
            logger.info("Could not determine file type.");
        }

        // TODO: Enhance with a DTOConverter or mapper.
        Content content = new Content();
        content.setTitle(title);
        content.setImage(imgUrl);
        contentRepository.save(content);

        contentDto.setImgUrl(content.getImage());
        contentDto.setTitle(content.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contentDto);
    }

}
