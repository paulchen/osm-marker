package at.rueckgr.osm.marker.service;

public enum ConfigurationKey {
    UPLOAD_DIRECTORY("upload_directory");

    private final String databaseKey;

    ConfigurationKey(final String databaseKey) {
        this.databaseKey = databaseKey;
    }

    public String getDatabaseKey() {
        return databaseKey;
    }
}
