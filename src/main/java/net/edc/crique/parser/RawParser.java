package net.edc.crique.parser;

import java.io.File;

public class RawParser implements DepotParser<byte[]> {
    public byte[] parse(File file, byte[] fileContent) {
        return fileContent;
    }
}
