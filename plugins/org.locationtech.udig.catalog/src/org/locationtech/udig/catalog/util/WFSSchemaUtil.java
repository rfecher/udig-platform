package org.locationtech.udig.catalog.util;

import java.net.URI;

import org.geotools.wfs.WFS;

public class WFSSchemaUtil {

    private static URI schemaNamespace = null;
    public static synchronized URI getWFSSchemaNamespaceCreateIfNull() {
        if (schemaNamespace == null) {
            schemaNamespace = URI.create(WFS.NAMESPACE);
        }
        return schemaNamespace;
    }

}
