package net.edc.crique.parser;

import java.io.File;

public interface DepotParser<T> {
  T parse(File file, byte[] fileContent);
}
