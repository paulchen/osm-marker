package at.rueckgr.osm.marker.component;

import at.rueckgr.osm.marker.entity.Marker;
import at.rueckgr.osm.marker.repository.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Component
public class MarkerService {
    @Autowired
    private MarkerRepository markerRepository;

    public List<Marker> getAllMarkers() {
        return markerRepository.findAll();
    }

    public Marker saveNewMarker(final Marker newMarker) {
        notNull(newMarker, "newMarker must not be null");

        return markerRepository.save(newMarker);
    }

    public Optional<Marker> findMarker(final Long markerId) {
        notNull(markerId, "markerId must not be null");

        return markerRepository.findById(markerId);
    }
}
