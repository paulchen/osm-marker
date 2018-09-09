package at.rueckgr.osm.marker.service;

import lombok.Data;
import org.springframework.core.io.Resource;


@Data
public class FileData {
    private final Resource resource;
    private final String filename;
    private final String contentType;
    private final Long size;
}
