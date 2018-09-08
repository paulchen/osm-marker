package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class UploadFileResponse {
    private final String fileName;
    private final String fileDownloadUri;
    private final String fileType;
    private final long size;
}