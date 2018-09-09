package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewMarkerInput {
    private final float latitude;
    private final float longitude;
    private final String name;
    private final List<Long> fileIds;
}
