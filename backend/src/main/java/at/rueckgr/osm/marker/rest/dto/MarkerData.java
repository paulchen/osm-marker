package at.rueckgr.osm.marker.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarkerData {
    private final List<MarkerDTO> markers;
}
