package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class UploadFileResponse {
    private final StatusDTO status;
    private final FileDTO fileData;
}
