/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiview;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTFTL;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTParameter;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTSignature;
import de.monticore.lang.templatesignature.templatesignature._parser.TemplateSignatureParser;
import de.se_rwth.commons.logging.Log;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ParserTest {
  public static final boolean ENABLE_FAIL_QUICK = true;
  
  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }
  
  @Test
  public void testParser() {
    TemplateSignatureParser parser = new TemplateSignatureParser();
    parser.setParserTarget(ParserExecution.EOF);
    Optional<ASTFTL> template = Optional.empty();
    try {
      template = parser
          .parse("src/test/resources/parser/valid/TemplateWithResult.ftl");
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    assertTrue(template.isPresent());
    List<ASTSignature> signatures = template.get().getSignatures();
    assertFalse(signatures.isEmpty());
    for(ASTSignature s : signatures){
      if(!s.getParamDecl().getParameters().isEmpty()){
        for(ASTParameter pd : s.getParamDecl().getParameters()){
          String output = pd.getType() + " " +pd.getName();
          System.out.println(output);
        }
      }
    }
  }
  
//  @Test
//  public void testMontiCoreFreemarkerParser() {
//    assertTrue(run("src/test/resources/parser/valid/TemplateWithResult.ftl"));
//  }
//  
//  protected boolean run(String filename) {
//    FreemarkerTool tool = new FreemarkerTool(new String[] { filename });
//    tool.init();
//    tool.setErrorLevel(Type.INFO);
//    boolean b = tool.run();
//   
//
//    return b;
//  }
  
  @Test
  public void testSomething() throws IOException{
    
    Configuration config = new Configuration();
    Path p = Paths.get("src/test/resources/parser/valid");
    config.setTemplateLoader(new FileTemplateLoader(p.toFile()));
    Template t = config.getTemplate("TemplateWithResult.ftl");
    Environment environment = new Environment(t, null, null);
  }
  
  // @Test
  // public void testParser() throws RecognitionException, IOException {
  //
  // TemplateSignatureParser parser = new TemplateSignatureParser();
  // parser.setParserTarget(ParserExecution.EOF);
  // Optional<de.monticore.lang.templatesignature.templatesignature._ast.ASTFTL>
  // asta = parser
  // .parse(new StringReader(
  // "this is template code which contains $ and other text <#-- This $ is a dollar sign @param int $ast @param double $d @result String -->"));
  // assertTrue(asta.isPresent());
  // assertFalse(asta.get().getOuterComments().isEmpty());
  // assertFalse(asta.get().getComments().isEmpty());
  // ASTSignature sig = asta.get().getComments().get(0).getSignatures().get(0);
  // System.out.println("Signatures: " +
  // asta.get().getComments().get(0).getSignatures().size());
  // System.out.println(" Comment " + sig.getComment().getWords());
  // System.out.println(sig.getParameters().get(0).getType() + " "
  // + sig.getParameters().get(0).getName());
  // System.out.println("ParamDecls: " + sig.getParameters().size());
  // System.out.println("Result " + sig.getResult());
  // }
  //
  // @Test
  // public void testSignatureWithoutResult() {
  // TemplateSignatureParser parser = new TemplateSignatureParser();
  // Optional<ASTFTL> template = Optional.empty();
  // try {
  // template = parser
  // .parse("src/test/resources/parser/valid/TemplateWithoutResult.ftl");
  // }
  // catch (RecognitionException | IOException e) {
  // e.printStackTrace();
  // }
  // assertTrue(template.isPresent());
  // List<ASTComment> comments = template.get().getComments();
  // List<ASTSignature> signatures = comments.get(0).getSignatures();
  // assertTrue(signatures.size() == 1);
  // ASTSignature signature = signatures.get(0);
  // assertTrue(signature.getParameters().size() == 2);
  // ASTParameter s1 = signature.getParameters().get(0);
  // String t1 = s1.getType();
  // String n1 = s1.getName();
  // assertEquals("int", t1);
  // assertEquals("$ast",n1);
  // assertFalse(signature.getResult().isPresent());
  // }
  //
  // @Test
  // public void testSignatureWithResult(){
  // TemplateSignatureParser parser = new TemplateSignatureParser();
  // Optional<ASTFTL> template = Optional.empty();
  // try {
  // template = parser
  // .parse("src/test/resources/parser/valid/TemplateWithResult.ftl");
  // }
  // catch (RecognitionException | IOException e) {
  // e.printStackTrace();
  // }
  // //@param int $ast @param double $bubu
  // assertTrue(template.isPresent());
  // List<ASTComment> comments = template.get().getComments();
  // List<ASTSignature> signatures = comments.get(0).getSignatures();
  // assertTrue(signatures.size() == 1);
  // ASTSignature signature = signatures.get(0);
  // assertTrue(signature.getParameters().size() == 2);
  // ASTParameter s2 = signature.getParameters().get(1);
  // String t2 = s2.getType();
  // String n2 = s2.getName();
  // assertEquals("double", t2);
  // assertEquals("$bubu",n2);
  // assertTrue(signature.getResult().isPresent());
  // ASTResult result = signature.getResult().get();
  // assertEquals("java.util.List", result.getType());
  // }
  //
  //
  // @Test
  // public void testTemplateWithoutSignature(){
  // TemplateSignatureParser parser = new TemplateSignatureParser();
  // Optional<ASTFTL> template = Optional.empty();
  // try {
  // template = parser
  // .parse("src/test/resources/parser/valid/TemplateWithoutSignature.ftl");
  // }
  // catch (RecognitionException | IOException e) {
  // e.printStackTrace();
  // }
  // assertTrue(template.isPresent());
  // List<ASTComment> comments = template.get().getComments();
  // for(ASTComment c : comments){
  // assertEquals(0, c.getSignatures().get(0).getParameters().size());
  // assertFalse(c.getSignatures().get(0).getResult().isPresent());
  // }
  // }
}
