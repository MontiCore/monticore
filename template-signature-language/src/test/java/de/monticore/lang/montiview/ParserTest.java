/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import de.monticore.lang.templatesignature.templatecontent._ast.ASTFTL;
import de.monticore.lang.templatesignature.templatecontent._parser.TemplateContentParser;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTComment;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTParameter;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTResult;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTSignature;
import de.monticore.lang.templatesignature.templatesignature._parser.TemplateSignatureParser;
import de.se_rwth.commons.logging.Log;

public class ParserTest {
  public static final boolean ENABLE_FAIL_QUICK = true;
  
  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }
  
  @Test
  public void testParser() throws RecognitionException, IOException {
    TemplateContentParser p = new TemplateContentParser();
    Optional<ASTFTL> ftl = p
        .parse_String("<#-- Generates  @param int $ast @param double $bubu @result java.util.List -->  asfdkasfdblaf <#if existsHWC>abstract</#if>");
    assertTrue(ftl.isPresent());
    ASTFTL f = ftl.get();
    System.out.println(f.getComments());
    TemplateSignatureParser sigParser = new TemplateSignatureParser();
    Optional<ASTComment> oSig = sigParser.parse_String(f.getComments().get(0));
    assertTrue(oSig.isPresent());
    ASTSignature s = oSig.get().getSignature();
    System.out.println("Params: " + s.getParameters());
    assertTrue(s.getResult().isPresent());
    System.out.println("Result: " + s.getResult().get().getResultType());
  }
  
  @Test
  public void testSignatureCorrectness() {
    File fqnTemplateName = new File("src/test/resources/parser/valid/TemplateWithResult.ftl");
    ASTSignature signature = parse(fqnTemplateName);
    List<ASTParameter> params = signature.getParameters();
    assertEquals(params.get(0).getName(), "$ast");
    assertEquals(params.get(0).getParamType().toString(), "int");
    Optional<ASTResult> res = signature.getResult();
    assertTrue(res.isPresent());
    assertEquals(res.get().getResultType().toString(), "java.util.List");
  }
  
  private ASTSignature parse(File fqnTemplateName) {
    FileReader fr;
    ASTSignature signature = null;
    try {
      fr = new FileReader(fqnTemplateName);
      System.out.println(fqnTemplateName.getAbsolutePath());
      BufferedReader br = new BufferedReader(fr);
      // String line = "";
      // line = br.readLine();
      // String signatureString = "";
      // while (line != null) {
      // signatureString += "\n" + line;
      // line = br.readLine();
      // }
      // br.close();
      
      TemplateContentParser contentParser = new TemplateContentParser();
      Optional<ASTFTL> oFTL = contentParser.parse(br);
      assertTrue(oFTL.isPresent());
      ASTFTL fTL = oFTL.get();
      List<String> comments = fTL.getComments();
      TemplateSignatureParser signatureParser = new TemplateSignatureParser();
      System.out.println("Found Comments: " + comments);
      Optional<ASTComment> oComment = signatureParser.parse_String(comments.get(0));
      assertTrue(oComment.isPresent());
      ASTComment comment = oComment.get();
      signature = comment.getSignature();
      // TemplateSignatureParser parser = new TemplateSignatureParser();
      // signature = parser.parseString_Signature(signatureString);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return signature;
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
          // signature = parser.parse(file.toString());
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
