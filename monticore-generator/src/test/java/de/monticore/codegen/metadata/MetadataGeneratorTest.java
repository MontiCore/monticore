package de.monticore.codegen.metadata;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.fail;

public class MetadataGeneratorTest {

  private static final String MODEL_PATH = "src/test/resources/";


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
  public void testGenerateMetadataWithNull() {
    ASTCDCompilationUnit cd = new ASTCDCompilationUnit();
    File targetDir = new File("");

    MetadataGenerator.generateMetadata(cd, targetDir);
  }
}
