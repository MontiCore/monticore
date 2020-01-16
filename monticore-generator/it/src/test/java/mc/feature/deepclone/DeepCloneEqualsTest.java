/* (c) https://github.com/MontiCore/monticore */
package mc.feature.deepclone;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import mc.feature.deepclone.deepclone._ast.*;
import mc.feature.deepclone.deepclone._parser.DeepCloneParser;
import mc.feature.deepclone.deepclone3._ast.ASTA;
import mc.feature.deepclone.deepclone3._parser.DeepClone3Parser;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class DeepCloneEqualsTest {
  
  @Test
  public void TestBasic() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneBasic> ast = parser.parse_StringCloneBasic("basic");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneBasic astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestName() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneName> ast = parser.parse_StringCloneName("Name");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneName astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestNameList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneNameList> ast = parser.parse_StringCloneNameList("Name1 Name2 Name3");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneNameList astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestNameOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneNameOptional> ast = parser.parse_StringCloneNameOptional("opt Name");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentName());
    ASTCloneNameOptional astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentName());
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestNameOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneNameOptional> ast = parser.parse_StringCloneNameOptional("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentName());
    ASTCloneNameOptional astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentName());
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestAST() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneAST> ast = parser.parse_StringCloneAST("clone Name1 Name2 Name3");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneAST astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestASTList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneASTList> ast = parser
        .parse_StringCloneASTList("clone Name1 Name2 clone Name3 Name4 Name5");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneASTList astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestASTOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneASTOptional> ast = parser
        .parse_StringCloneASTOptional("opt clone Name1 Name2 Name3");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentCloneAST());
    ASTCloneASTOptional astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentCloneAST());
    assertTrue(ast.get().deepEquals(astClone));
  }
  
  @Test
  public void TestASTOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneASTOptional> ast = parser.parse_StringCloneASTOptional("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentCloneAST());
    ASTCloneASTOptional astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentCloneAST());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestString() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneString> ast = parser.parse_StringCloneString("\"String\"");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneString astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestStringList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringList> ast = parser.parse_StringCloneStringList("\"String1\" \"String2\" \"String3\"");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneStringList astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestStringOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional> ast = parser.parse_StringCloneStringOptional("opt \"String\"");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentStringLiteral());
    ASTCloneStringOptional astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentStringLiteral());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestStringOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional> ast = parser.parse_StringCloneStringOptional("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentStringLiteral());
    ASTCloneStringOptional astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentStringLiteral());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestInt() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneInt> ast = parser.parse_StringCloneInt("1234");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneInt astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestIntList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntList> ast = parser.parse_StringCloneIntList("12 34 56");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneIntList astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestIntOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional> ast = parser.parse_StringCloneIntOptional("opt 234");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentIntLiteral());
    ASTCloneIntOptional astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentIntLiteral());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestIntOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional> ast = parser.parse_StringCloneIntOptional("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentIntLiteral());
    ASTCloneIntOptional astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentIntLiteral());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestString2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneString2> ast = parser.parse_StringCloneString2("\"String\"");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneString2 astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestStringList2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringList2> ast = parser.parse_StringCloneStringList2("\"String1\" \"String2\" \"String3\"");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneStringList2 astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestStringOptionalPresent2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional2> ast = parser.parse_StringCloneStringOptional2("opt \"String\"");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentString());
    ASTCloneStringOptional2 astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentString());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestStringOptionalAbsent2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional2> ast = parser.parse_StringCloneStringOptional2("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentString());
    ASTCloneStringOptional2 astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentString());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestInt2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneInt2> ast = parser.parse_StringCloneInt2("1234");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneInt2 astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestIntList2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntList2> ast = parser.parse_StringCloneIntList2("12 34 56");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneIntList2 astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestIntOptional2Present() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt 234");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentNum_Int());
    ASTCloneIntOptional2 astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentNum_Int());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestIntOptional2Absent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentNum_Int());
    ASTCloneIntOptional2 astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentNum_Int());
    assertTrue(ast.get().deepEquals(astClone));
  }

 /* @Test
  public void TestEnumList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneEnumList> ast = parser.parse_StringCloneEnumList("enum");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    ASTCloneEnumList astClone = ast.get().deepClone();
    assertTrue(ast.get().deepEquals(astClone));
  }*/

  @Test
  public void TestEnumOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneEnumOptional> ast = parser.parse_StringCloneEnumOptional("opt enum");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(ast.get().isPresentCloneEnum());
    ASTCloneEnumOptional astClone = ast.get().deepClone();
    assertTrue(astClone.isPresentCloneEnum());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void TestEnumOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneEnumOptional> ast = parser.parse_StringCloneEnumOptional("opt");
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertFalse(ast.get().isPresentCloneEnum());
    ASTCloneEnumOptional astClone = ast.get().deepClone();
    assertFalse(astClone.isPresentCloneEnum());
    assertTrue(ast.get().deepEquals(astClone));
  }

  @Test
  public void testFileNameInSourcePosition() throws IOException {
    String grammarToTest = "src/main/grammars/mc/feature/deepclone/DeepClone3.mc4";

    Path model = Paths.get(new File(
        grammarToTest).getAbsolutePath());

    DeepClone3Parser parser = new DeepClone3Parser();
    //hier muss doch MontiCoreScript benutzt werden oder? Oder wie kann sonst ASTMCGrammar und nicht ASTA geparsed weren?
    Optional<ASTMCGrammar> ast = parser.parse(model.toString());

    assertTrue(ast.isPresent());
    ASTMCGrammar clonedAst = ast.get().deepClone();
    assertTrue(clonedAst.get_SourcePositionStart().getFileName().isPresent());
    assertEquals("DeepClone3.mc4", FilenameUtils.getName(clonedAst.get_SourcePositionStart().getFileName().get()));
  }
}
