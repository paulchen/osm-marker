package at.rueckgr.osm.marker.rest.controller;

import at.rueckgr.osm.marker.component.MarkerService;
import at.rueckgr.osm.marker.rest.dto.MarkerDTO;
import at.rueckgr.osm.marker.rest.dto.MarkerData;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/osm")
public class MarkerController {

    @Autowired
    private MarkerService markerService;

    @RequestMapping("/marker")
    public MarkerData getAllMarkers() {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        final MapperFacade mapper = mapperFactory.getMapperFacade();

        final List<MarkerDTO> markerDTOs = markerService
                .getAllMarkers()
                .stream()
                .map(entity -> mapper.map(entity, MarkerDTO.class))
                .collect(Collectors.toList());

        return new MarkerData(markerDTOs);
    }
}
