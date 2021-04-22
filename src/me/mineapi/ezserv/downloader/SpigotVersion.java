package me.mineapi.ezserv.downloader;

public class SpigotVersion {
    String version;
    String url;
    public SpigotVersion(String version, String url) {
        this.version = version;
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Version [version=" + version + ", url=" + url + "]";
    }
}
