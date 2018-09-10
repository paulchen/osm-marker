package at.rueckgr.osm.marker.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity(name = "file")
@Data
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @NotNull
    @Column(nullable = false)
    private String actualFilename;

    @NotNull
    @Column(nullable = false)
    private String contentType;

    @ManyToOne
    private Marker marker;

    @NotNull
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}
