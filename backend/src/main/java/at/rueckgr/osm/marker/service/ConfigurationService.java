package at.rueckgr.osm.marker.service;

import at.rueckgr.osm.marker.entity.Configuration;
import at.rueckgr.osm.marker.exception.ConfigurationKeyNotFoundException;
import at.rueckgr.osm.marker.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public String getStringConfiguration(final ConfigurationKey key) throws ConfigurationKeyNotFoundException {
        notNull(key, "key must not be null");

        final Optional<Configuration> optional = configurationRepository.findById(key.getDatabaseKey());
        if (!optional.isPresent()) {
            throw new ConfigurationKeyNotFoundException("Configuration key not found: " + key);
        }

        return optional.get().getValue();
    }
}
