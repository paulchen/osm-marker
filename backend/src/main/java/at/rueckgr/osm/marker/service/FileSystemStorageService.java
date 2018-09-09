package at.rueckgr.osm.marker.service;

import at.rueckgr.osm.marker.entity.File;
import at.rueckgr.osm.marker.exception.ConfigurationKeyNotFoundException;
import at.rueckgr.osm.marker.exception.StorageException;
import at.rueckgr.osm.marker.exception.StorageFileNotFoundException;
import at.rueckgr.osm.marker.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Service
public class FileSystemStorageService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ConfigurationService configurationService;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        if (rootLocation == null) {
            try {
                rootLocation = Paths.get(configurationService.getStringConfiguration(ConfigurationKey.UPLOAD_DIRECTORY));
            } catch (ConfigurationKeyNotFoundException e) {
                throw new StorageException("Upload directory not configured", e);
            }

            try {
                Files.createDirectories(rootLocation);
            } catch (IOException e) {
                throw new StorageException("Could not initialize storage", e);
            }
        }
    }

    public Long store(final MultipartFile file) {
        notNull(file, "file must not be null");

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }

            File fileEntity = new File();
            fileEntity.setActualFilename(filename);
            fileEntity.setContentType(file.getContentType());
            fileEntity = fileRepository.save(fileEntity);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, getFilesystemPath(fileEntity));
            }

            return fileEntity.getId();
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    private Path getFilesystemPath(final File fileEntity) {
        notNull(fileEntity, "fileEntity must not be null");
        notNull(fileEntity.getId(), "fileEntity must have an id");

        final String filename = "file_" + String.format("%05d", fileEntity.getId());
        return this.rootLocation.resolve(filename);
    }

    public FileData loadAsResource(final Long id) {
        notNull(id, "id must not be null");

        try {
            final Optional<File> optionalFile = fileRepository.findById(id);
            if (!optionalFile.isPresent()) {
                throw new StorageFileNotFoundException("File not found with id " + id);
            }

            final File fileEntity = optionalFile.get();
            final Path file = getFilesystemPath(fileEntity);
            final Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return new FileData(resource, fileEntity.getActualFilename(), fileEntity.getContentType(), Files.size(file));
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file with id " + id);

            }
        }
        catch (IOException e) {
            throw new StorageFileNotFoundException("Could not read file with id " + id, e);
        }
    }
}
