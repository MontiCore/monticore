/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.mcbasictypes1._ast.ASTBooleanType;
import de.monticore.mcbasictypes1._ast.ASTByteType;
import de.monticore.mcbasictypes1._ast.ASTCharType;
import de.monticore.mcbasictypes1._ast.ASTDoubleType;
import de.monticore.mcbasictypes1._ast.ASTImportStatement;
import de.monticore.mcbasictypes1._ast.ASTIntType;
import de.monticore.mcbasictypes1._ast.ASTLongType;
import de.monticore.mcbasictypes1._ast.ASTNameAsReferenceType;
import de.monticore.mcbasictypes1._ast.ASTQualifiedName;
import de.monticore.mcbasictypes1._ast.ASTShortType;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicTypes1PrettyPrinter;
import de.monticore.testmcbasictypes1._parser.TestMCBasicTypes1Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class MCBasicTypes1PrettyPrinterTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void testQualifiedName() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTQualifiedName> result = parser
        .parseQualifiedName(new StringReader("name.Name._na2me.$4name"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTQualifiedName qualifiedName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(qualifiedName);
    
    result = parser.parseQualifiedName(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(qualifiedName.deepEquals(result.get()));
  }
  
  @Test
  public void testImportStatement1() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTImportStatement> result = parser
        .parseImportStatement(new StringReader("import A.b;"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTImportStatement importstatement = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(importstatement);
    
    result = parser.parseImportStatement(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(importstatement.deepEquals(result.get()));
  }
  
  @Test
  public void testImportStatement2() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTImportStatement> result = parser
        .parseImportStatement(new StringReader("import Name.*;"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTImportStatement importstatement = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(importstatement);
    
    result = parser.parseImportStatement(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(importstatement.deepEquals(result.get()));
  }
  
  @Test
  public void testBooleanType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTBooleanType> result = parser.parseBooleanType(new StringReader("boolean"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBooleanType booleanName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(booleanName);
    
    result = parser.parseBooleanType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(booleanName.deepEquals(result.get()));
  }
  
  @Test
  public void testByteType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTByteType> result = parser.parseByteType(new StringReader("byte"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTByteType byteName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(byteName);
    
    result = parser.parseByteType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(byteName.deepEquals(result.get()));
  }
  
  @Test
  public void testCharType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTCharType> result = parser.parseCharType(new StringReader("char"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCharType charName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(charName);
    
    result = parser.parseCharType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(charName.deepEquals(result.get()));
  }
  
  @Test
  public void testShortType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTShortType> result = parser.parseShortType(new StringReader("short"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTShortType shortName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(shortName);
    
    result = parser.parseShortType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(shortName.deepEquals(result.get()));
  }
  
  @Test
  public void testIntType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTIntType> result = parser.parseIntType(new StringReader("int"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIntType intName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(intName);
    
    result = parser.parseIntType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(intName.deepEquals(result.get()));
  }
  
  @Test
  public void testLongType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTLongType> result = parser.parseLongType(new StringReader("long"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLongType longName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(longName);
    
    result = parser.parseLongType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(longName.deepEquals(result.get()));
  }
  
  @Test
  public void testDoubleType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTDoubleType> result = parser.parseDoubleType(new StringReader("double"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDoubleType doubleName = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(doubleName);
    
    result = parser.parseDoubleType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(doubleName.deepEquals(result.get()));
  }
  
  @Test
  public void testNameAsReferenceType() throws IOException {
    TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
    Optional<ASTNameAsReferenceType> result = parser
        .parseNameAsReferenceType(new StringReader("Name.name2.$end"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTNameAsReferenceType nameAsReferenceType = result.get();
    
    MCBasicTypes1PrettyPrinter prettyPrinter = new MCBasicTypes1PrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(nameAsReferenceType);
    
    result = parser.parseNameAsReferenceType(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(nameAsReferenceType.deepEquals(result.get()));
  }
}
