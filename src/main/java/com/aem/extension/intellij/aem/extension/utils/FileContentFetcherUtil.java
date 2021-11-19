package com.aem.extension.intellij.aem.extension.utils;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;

import com.intellij.openapi.diagnostic.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public final class FileContentFetcherUtil {

  private static final Logger LOGGER = Logger.getInstance(FileContentFetcherUtil.class);

  private FileContentFetcherUtil() {
  }

  public static String getFileContentFromTemplate(String templatePath) {
    String fileContent;
    try (InputStream resourceAsStream = FileContentFetcherUtil.class.getClassLoader()
        .getResourceAsStream(templatePath)) {
      if (resourceAsStream == null) {
        LOGGER.error(String.format("Resource by template path [%s] wasn't found.", templatePath));
        return EMPTY_STRING;
      }
      fileContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      LOGGER.error(String.format("IOException when reading the file: %s", e.getMessage()));
      return EMPTY_STRING;
    }
    return fileContent;
  }
}
