/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiview;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.lang.templatesignature.templatesignature._ast.ASTSignature;
import de.monticore.lang.templatesignature.templatesignature._parser.TemplateSignatureParser;
import de.se_rwth.commons.logging.Log;

/**
 * @author Robert Heim
 */
public class ParserTest {
  public static final boolean ENABLE_FAIL_QUICK = true;
  
  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }
  
  
  @Test
  public void testSignature() throws RecognitionException, IOException {
    test("si");
  }
  
  
  private void test(String fileEnding) throws IOException {
    ParseTest parserTest = new ParseTest("." + fileEnding);
    Files.walkFileTree(Paths.get("src/test/resources/"), parserTest);
    
    if (!parserTest.getModelsInError().isEmpty()) {
      Log.debug("Models in error", "ParserTest");
      for (String model : parserTest.getModelsInError()) {
        Log.debug("  " + model, "ParserTest");
      }
    }
    Log.info("Count of tested models: " + parserTest.getTestCount(), "ParserTest");
    Log.info("Count of correctly parsed models: "
        + (parserTest.getTestCount() - parserTest.getModelsInError().size()), "ParserTest");
        
    assertTrue("There were models that could not be parsed", parserTest.getModelsInError()
        .isEmpty());
  }
  
  /**
   * Visits files of the given file ending and checks whether they are parsable.
   * 
   * @author Robert Heim
   * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
   */
  private static class ParseTest extends SimpleFileVisitor<Path> {
    
    private String fileEnding;
    
    private List<String> modelsInError = new ArrayList<>();
    
    private int testCount = 0;
    
    public ParseTest(String fileEnding) {
      super();
      this.fileEnding = fileEnding;
    }
    
    /**
     * @return testCount
     */
    public int getTestCount() {
      return this.testCount;
    }
    
    /**
     * @return modelsInError
     */
    public List<String> getModelsInError() {
      return this.modelsInError;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
      if (file.toFile().isFile()
          && (file.toString().toLowerCase().endsWith(fileEnding))) {
          
        Log.debug("Parsing file " + file.toString(), "ParserTest");
        testCount++;
        
        Optional<ASTSignature> signature = Optional.empty();        
        TemplateSignatureParser parser = new TemplateSignatureParser();
        try {
          signature = parser.parse(file.toString());
        }
        catch (Exception e) {
            Log.error("Exception during test", e);
          }
        if ((parser.hasErrors() || !signature.isPresent())) {
          modelsInError.add(file.toString());
          Log.error("There were unexpected parser errors");
        }
        else {
          Log.getFindings().clear();
        }
        Log.enableFailQuick(ParserTest.ENABLE_FAIL_QUICK);
      }
      return FileVisitResult.CONTINUE;
    }
  };
  
}
