package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class StatusDTO {
    private final ReturnCode returnCode;
    private final String message;
}
