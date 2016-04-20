/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTComment;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTFTL;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTParamDecl;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTResult;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTSignature;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTType;
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
    
    TemplateSignatureParser parser = new TemplateSignatureParser();
    parser.setParserTarget(ParserExecution.EOF);
    Optional<de.monticore.lang.templatesignature.templatesignature._ast.ASTFTL> asta = parser
        .parse(new StringReader(
            "this is template code which contains $ and other text <#-- This $ is a dollar sign @param int $ast @param double $d @result String -->"));
    assertTrue(asta.isPresent());
    assertFalse(asta.get().getOuterComments().isEmpty());
    assertFalse(asta.get().getComments().isEmpty());
    ASTSignature sig = asta.get().getComments().get(0).getSignatures().get(0);
    System.out.println("Signatures: " + asta.get().getComments().get(0).getSignatures().size());
    System.out.println(" Comment " + sig.getComment().getWords());
    System.out.println(sig.getParamDecls().get(0).getParamDef().getType().getWords().get(0) + " "
        + sig.getParamDecls().get(0).getParamDef().getName());
    System.out.println("ParamDecls: " + sig.getParamDecls().size());
    System.out.println("Result " + sig.getResult());
  }
  
  @Test
  public void testSignatureWithoutResult() {
    TemplateSignatureParser parser = new TemplateSignatureParser();
    Optional<ASTFTL> template = Optional.empty();
    try {
      template = parser
          .parse("src/test/resources/parser/valid/TemplateWithoutResult.ftl");
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    assertTrue(template.isPresent());
    List<ASTComment> comments = template.get().getComments();
    List<ASTSignature> signatures = comments.get(0).getSignatures();
    assertTrue(signatures.size() == 1);
    ASTSignature signature = signatures.get(0);
    assertTrue(signature.getParamDecls().size() == 2);
    ASTParamDecl s1 = signature.getParamDecls().get(0);
    ASTType t1 = s1.getParamDef().getType();
    String n1 = s1.getParamDef().getName();
    assertEquals("int", t1.getWords().get(0));
    assertEquals("$ast",n1);
    assertFalse(signature.getResult().isPresent());
  }
  
  @Test
  public void testSignatureWithResult(){
    TemplateSignatureParser parser = new TemplateSignatureParser();
    Optional<ASTFTL> template = Optional.empty();
    try {
      template = parser
          .parse("src/test/resources/parser/valid/TemplateWithResult.ftl");
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    //@param int $ast @param double $bubu
    assertTrue(template.isPresent());
    List<ASTComment> comments = template.get().getComments();
    List<ASTSignature> signatures = comments.get(0).getSignatures();
    assertTrue(signatures.size() == 1);
    ASTSignature signature = signatures.get(0);
    assertTrue(signature.getParamDecls().size() == 2);
    ASTParamDecl s2 = signature.getParamDecls().get(1);
    ASTType t2 = s2.getParamDef().getType();
    String n2 = s2.getParamDef().getName();
    assertEquals("double", t2.getWords().get(0));
    assertEquals("$bubu",n2);
    assertTrue(signature.getResult().isPresent());
    ASTResult result = signature.getResult().get();
    assertEquals("java.util.List", result.getType().getWords().get(0));
  }
  
  
  @Test
  public void testTemplateWithoutSignature(){
    TemplateSignatureParser parser = new TemplateSignatureParser();
    Optional<ASTFTL> template = Optional.empty();
    try {
      template = parser
          .parse("src/test/resources/parser/valid/TemplateWithoutSignature.ftl");
    }
    catch (RecognitionException | IOException e) {
      e.printStackTrace();
    }
    assertTrue(template.isPresent());
    List<ASTComment> comments = template.get().getComments();
    for(ASTComment c : comments){
      assertEquals(0, c.getSignatures().get(0).getParamDecls().size());
      assertFalse(c.getSignatures().get(0).getResult().isPresent());
    }
  }
}
