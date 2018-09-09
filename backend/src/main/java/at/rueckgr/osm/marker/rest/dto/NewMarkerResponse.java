package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class NewMarkerResponse {
    private final StatusDTO status;
    private final MarkerDTO marker;
}
