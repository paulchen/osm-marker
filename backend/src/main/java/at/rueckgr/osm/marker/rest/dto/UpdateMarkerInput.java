package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateMarkerInput {
    private final String name;
    private final String link;
    private final List<Long> fileIds;
}
