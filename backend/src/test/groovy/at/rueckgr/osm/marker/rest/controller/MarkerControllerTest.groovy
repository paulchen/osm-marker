package at.rueckgr.osm.marker.rest.controller

import at.rueckgr.osm.marker.component.MarkerService
import at.rueckgr.osm.marker.service.FileSystemStorageService
import spock.lang.Specification

class MarkerControllerTest extends Specification {
    def markerService = Mock(MarkerService)
    def storageService = Mock(FileSystemStorageService)
    def markerController = new MarkerController()

    @SuppressWarnings("GroovyAccessibility")
    void setup() {
        markerController.markerService = this.markerService
        markerController.storageService = this.storageService
    }

    def "noMarkerStored"() {
        given:

        when:
        def allMarkers = markerController.getAllMarkers()

        then:
        1 * markerService.getAllMarkers() >> []
        allMarkers.markers.isEmpty()
    }
}
