package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class UploadFileResponse {
    private final Long id;
    private final String fileName;
    private final String fileType;
    private final long size;
}
