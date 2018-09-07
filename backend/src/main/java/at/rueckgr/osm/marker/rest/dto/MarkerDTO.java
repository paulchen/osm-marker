package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class MarkerDTO {
    private final Long id;
    private final float latitude;
    private final float longitude;
    private final String name;
}
