package de.monticore.codegen.metadata;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MetadataGeneratorTest {


  @Test
  public void testOpenGradlePropertiesFile() {
    Properties properties = new Properties();

    try {
      BufferedInputStream stream = new BufferedInputStream(new FileInputStream("gradle.properties"));
      properties.load(stream);
      stream.close();
    } catch(IOException e) {
      Assert.fail();
    }

    System.out.println("Version: " + properties.getProperty("version"));
  }

  @Test
  public void testGenerateMetadata() {

  }
}
