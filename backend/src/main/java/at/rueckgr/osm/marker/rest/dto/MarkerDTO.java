package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

@Data
public class MarkerDTO {
    private Long id;
    private float latitude;
    private float longitude;
    private String name;
    private String link;
    private int uploads;
}
