package at.rueckgr.osm.marker.component;

import at.rueckgr.osm.marker.entity.Marker;
import at.rueckgr.osm.marker.repository.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarkerService {
    @Autowired
    private MarkerRepository markerRepository;

    public List<Marker> getAllMarkers() {
        return markerRepository.findAll();
    }
}
