package at.rueckgr.osm.marker.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity(name = "configuration")
@Data
public class Configuration {
    @Id
    @Column(name = "configuration_key", length = 100)
    private String key;

    @Version
    private Long version;

    @NotNull
    @Column(nullable = false)
    private String value;
}
