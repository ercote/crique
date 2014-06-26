package com.lq.monitor;

import java.io.File;
import java.util.List;

public interface DirectorySelector {
  public List<File> listFiles(File dir);
}
