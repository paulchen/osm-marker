package at.rueckgr.osm.marker.rest.controller;

import at.rueckgr.osm.marker.component.MarkerService;
import at.rueckgr.osm.marker.entity.File;
import at.rueckgr.osm.marker.entity.Marker;
import at.rueckgr.osm.marker.exception.StorageException;
import at.rueckgr.osm.marker.rest.dto.FileDTO;
import at.rueckgr.osm.marker.rest.dto.MarkerDTO;
import at.rueckgr.osm.marker.rest.dto.MarkerData;
import at.rueckgr.osm.marker.rest.dto.NewMarkerInput;
import at.rueckgr.osm.marker.rest.dto.NewMarkerResponse;
import at.rueckgr.osm.marker.rest.dto.ReturnCode;
import at.rueckgr.osm.marker.rest.dto.StatusDTO;
import at.rueckgr.osm.marker.rest.dto.UpdateMarkerInput;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

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
        final List<MarkerDTO> markerDTOs = markerService
                .getAllMarkers()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        return new MarkerData(markerDTOs);
    }

    private MarkerDTO entityToDto(final Marker marker) {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        final MapperFacade mapper = mapperFactory.getMapperFacade();

        final MarkerDTO markerDTO = mapper.map(marker, MarkerDTO.class);
        // TODO n+1 problem?
        markerDTO.setUploads(marker.getFiles().size());
        return markerDTO;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/marker")
    public NewMarkerResponse saveNewMarker(@RequestBody final NewMarkerInput newMarkerInput) {
        try {
            notNull(newMarkerInput, "newMarkerInput must not be null");

            final Marker newMarker = new Marker();
            // TODO orika
            newMarker.setLatitude(newMarkerInput.getLatitude());
            newMarker.setLongitude(newMarkerInput.getLongitude());
            newMarker.setName(newMarkerInput.getName());
            newMarker.setLink(newMarkerInput.getLink());
            newMarker.setLastUpdated(LocalDateTime.now());

            if (newMarkerInput.getFileIds() != null) {
                final List<File> fileList = newMarkerInput.getFileIds().stream()
                        .map(fileId -> storageService.getFileEntity(fileId))
                        .peek(this::checkNotAssociated)
                        .peek(file -> file.setMarker(newMarker))
                        .collect(Collectors.toList());
                newMarker.setFiles(fileList);
            }
            else {
                newMarker.setFiles(Collections.emptyList());
            }

            final Marker savedMarker = markerService.saveNewMarker(newMarker);
            final MarkerDTO markerDTO = entityToDto(savedMarker);
            final StatusDTO statusDTO = new StatusDTO(ReturnCode.OK, "ok");

            return new NewMarkerResponse(statusDTO, markerDTO);
        }
        catch (Exception e) {
            // TODO log exception
            final StatusDTO status = new StatusDTO(ReturnCode.GENERAL_ERROR, e.getMessage());
            return new NewMarkerResponse(status, null);
        }
    }

    private void checkNotAssociated(final File file) throws StorageException {
        notNull(file, "file must not be null");

        if (file.getMarker() != null) {
            throw new StorageException("File " + file.getId() + " already associated with marker " + file.getMarker().getId());
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            notNull(file, "file must not be null");

            final Long id = storageService.store(file);

            final StatusDTO status = new StatusDTO(ReturnCode.OK, "ok");
            // TODO content type always application/octet-stream?
            final FileDTO fileData = new FileDTO(id, file.getName(), file.getContentType(), file.getSize());

            return new UploadFileResponse(status, fileData);
        }
        catch (Exception e) {
            // TODO log exception
            final StatusDTO status = new StatusDTO(ReturnCode.GENERAL_ERROR, e.getMessage());
            return new UploadFileResponse(status, null);
        }
    }

    @GetMapping("/files/{id:[0-9]+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Long id) {
        try {
            notNull(id, "id must not be null");

            final FileData fileData = storageService.loadAsResource(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, fileData.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.getSize()))
                    .body(fileData.getResource());
        }
        catch (Exception e) {
            // TODO log exception
            return ResponseEntity.badRequest().body(null);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/marker/{markerId:[0-9]+}/uploads")
    public List<FileDTO> loadExistingUploads(@PathVariable final Long markerId) {
        notNull(markerId, "markerId must not be null");

        final Optional<Marker> optional = markerService.findMarker(markerId);
        if (!optional.isPresent()) {
            // TODO scream
            throw new IllegalArgumentException();
        }

        return optional.get()
                .getFiles()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    private FileDTO entityToDto(final File file) {
        // TODO file size
        return new FileDTO(file.getId(), file.getActualFilename(), file.getContentType(), 1L);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/marker/{markerId:[0-9]+}")
    @Transactional
    public NewMarkerResponse updateMarker(@PathVariable final Long markerId, @RequestBody final UpdateMarkerInput updateMarkerInput) {
        try {
            notNull(markerId, "markerId must not be null");

            final Optional<Marker> optional = markerService.findMarker(markerId);
            if (!optional.isPresent()) {
                // TODO scream
                throw new IllegalArgumentException();
            }
            final Marker marker = optional.get();

            marker.setName(updateMarkerInput.getName());
            marker.setLink(updateMarkerInput.getLink());
            // TODO only if really something changed
            marker.setLastUpdated(LocalDateTime.now());

            // TODO Java-8-style way?
            final Iterator<File> iterator = marker.getFiles().iterator();
            while (iterator.hasNext()) {
                final File file = iterator.next();
                if (!updateMarkerInput.getFileIds().contains(file.getId())) {
                    iterator.remove();
                    storageService.removeUpload(file);
                }
            }

            marker.getFiles().removeIf(file -> !updateMarkerInput.getFileIds().contains(file.getId()));

            final Set<Long> existingFileIds = marker.getFiles().stream().map(File::getId).collect(Collectors.toSet());
            updateMarkerInput.getFileIds()
                    .stream()
                    .filter(fileId -> !existingFileIds.contains(fileId))
                    .map(fileId -> storageService.getFileEntity(fileId))
                    .peek(this::checkNotAssociated)
                    .peek(file -> file.setMarker(marker))
                    .forEach(file -> marker.getFiles().add(file));

            final MarkerDTO markerDTO = entityToDto(marker);
            final StatusDTO statusDTO = new StatusDTO(ReturnCode.OK, "ok");
            return new NewMarkerResponse(statusDTO, markerDTO);
        }
        catch (Exception e) {
            // TODO log exception
            final StatusDTO status = new StatusDTO(ReturnCode.GENERAL_ERROR, e.getMessage());
            return new NewMarkerResponse(status, null);
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/marker/{markerId:[0-9]+}")
    @Transactional
    public void updateMarker(@PathVariable final Long markerId) {
        // TODO error handling
        notNull(markerId, "markerId must not be null");

        final Optional<Marker> optional = markerService.findMarker(markerId);
        if (!optional.isPresent()) {
            // TODO scream
            return;
        }

        final Marker marker = optional.get();
        marker.getFiles().forEach(file -> storageService.removeUpload(file));
        markerService.deleteMarker(marker);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/files/{fileId:[0-9]+}")
    @Transactional
    public void deleteFile(@PathVariable final Long fileId) {
        // TODO error handling
        notNull(fileId, "fileId must not be null");

        final File fileEntity = storageService.getFileEntity(fileId);
        storageService.removeUpload(fileEntity);
    }
}
