package at.rueckgr.osm.marker.rest.controller;

import at.rueckgr.osm.marker.component.MarkerService;
import at.rueckgr.osm.marker.rest.dto.MarkerDTO;
import at.rueckgr.osm.marker.rest.dto.MarkerData;
import at.rueckgr.osm.marker.rest.dto.UploadFileResponse;
import at.rueckgr.osm.marker.service.FileData;
import at.rueckgr.osm.marker.service.FileSystemStorageService;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/osm")
public class MarkerController {

    @Autowired
    private MarkerService markerService;

    @Autowired
    private FileSystemStorageService storageService;

    @CrossOrigin(origins = "*")
    @RequestMapping("/marker")
    public MarkerData getAllMarkers() {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        final MapperFacade mapper = mapperFactory.getMapperFacade();

        final List<MarkerDTO> markerDTOs = markerService
                .getAllMarkers()
                .stream()
                .map(entity -> mapper.map(entity, MarkerDTO.class))
                .collect(Collectors.toList());

        return new MarkerData(markerDTOs);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        final Long id = storageService.store(file);

        return new UploadFileResponse(id, file.getName(), file.getContentType(), file.getSize());
    }

    @GetMapping("/files/{id:[0-9]+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Long id) {
        final FileData fileData = storageService.loadAsResource(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileData.getContentType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.getSize()))
                .body(fileData.getResource());
    }
}
