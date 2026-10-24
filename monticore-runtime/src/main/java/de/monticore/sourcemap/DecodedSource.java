package de.monticore.sourcemap;

import java.net.URL;
import java.util.Objects;

public class DecodedSource {

    public final URL url;

    public final String content;

    public final boolean ignored;

    public DecodedSource(URL url) {
        this.url = url;
        this.content = null;
        this.ignored = false;
    }

    public DecodedSource(URL url, String content) {
        this.url = url;
        this.content = content;
        this.ignored = false;
    }

    public DecodedSource(URL url, String content, boolean ignored) {
        this.url = url;
        this.content = content;
        this.ignored = ignored;
    }

    @Override
    public String toString() {
        return "{"
                +url.toString()+","
                +content+","
                +ignored
                +"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DecodedSource that = (DecodedSource) obj;
        return Objects.equals(url, that.url);
    }

}