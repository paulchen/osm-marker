package at.rueckgr.osm.marker.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity(name = "marker")
@Data
public class Marker {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @NotNull
    @Column(nullable = false)
    private float latitude;

    @NotNull
    @Column(nullable = false)
    private float longitude;

    @NotNull
    @Column
    private String name;

    @OneToMany(mappedBy = "marker", cascade = { CascadeType.ALL })
    private List<File> files;
}
