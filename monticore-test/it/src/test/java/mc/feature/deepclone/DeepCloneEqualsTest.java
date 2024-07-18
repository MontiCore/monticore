/* (c) https://github.com/MontiCore/monticore */
package mc.feature.deepclone;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.deepclone.deepclone._ast.*;
import mc.feature.deepclone.deepclone._parser.DeepCloneParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class DeepCloneEqualsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void TestBasic() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneBasic> ast = parser.parse_StringCloneBasic("basic");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneBasic astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestName() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneName> ast = parser.parse_StringCloneName("Name");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneName astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestNameList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneNameList> ast = parser.parse_StringCloneNameList("Name1 Name2 Name3");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneNameList astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestNameOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneNameOptional> ast = parser.parse_StringCloneNameOptional("opt Name");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentName());
    ASTCloneNameOptional astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentName());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestNameOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneNameOptional> ast = parser.parse_StringCloneNameOptional("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentName());
    ASTCloneNameOptional astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentName());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestAST() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneAST> ast = parser.parse_StringCloneAST("clone Name1 Name2 Name3");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneAST astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestASTList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneASTList> ast = parser
        .parse_StringCloneASTList("clone Name1 Name2 clone Name3 Name4 Name5");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneASTList astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestASTOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneASTOptional> ast = parser
        .parse_StringCloneASTOptional("opt clone Name1 Name2 Name3");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentCloneAST());
    ASTCloneASTOptional astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentCloneAST());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void TestASTOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneASTOptional> ast = parser.parse_StringCloneASTOptional("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentCloneAST());
    ASTCloneASTOptional astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentCloneAST());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestString() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneString> ast = parser.parse_StringCloneString("\"String\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneString astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestStringList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringList> ast = parser.parse_StringCloneStringList("\"String1\" \"String2\" \"String3\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneStringList astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestStringOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional> ast = parser.parse_StringCloneStringOptional("opt \"String\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentStringLiteral());
    ASTCloneStringOptional astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentStringLiteral());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestStringOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional> ast = parser.parse_StringCloneStringOptional("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentStringLiteral());
    ASTCloneStringOptional astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentStringLiteral());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestInt() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneInt> ast = parser.parse_StringCloneInt("1234");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneInt astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestIntList() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntList> ast = parser.parse_StringCloneIntList("12 34 56");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneIntList astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestIntOptionalPresent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional> ast = parser.parse_StringCloneIntOptional("opt 234");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentIntLiteral());
    ASTCloneIntOptional astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentIntLiteral());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestIntOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional> ast = parser.parse_StringCloneIntOptional("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentIntLiteral());
    ASTCloneIntOptional astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentIntLiteral());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestString2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneString2> ast = parser.parse_StringCloneString2("\"String\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneString2 astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestStringList2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringList2> ast = parser.parse_StringCloneStringList2("\"String1\" \"String2\" \"String3\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneStringList2 astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestStringOptionalPresent2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional2> ast = parser.parse_StringCloneStringOptional2("opt \"String\"");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentString());
    ASTCloneStringOptional2 astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentString());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestStringOptionalAbsent2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneStringOptional2> ast = parser.parse_StringCloneStringOptional2("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentString());
    ASTCloneStringOptional2 astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentString());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestInt2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneInt2> ast = parser.parse_StringCloneInt2("1234");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneInt2 astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestIntList2() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntList2> ast = parser.parse_StringCloneIntList2("12 34 56");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    ASTCloneIntList2 astClone = ast.get().deepClone();
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestIntOptional2Present() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt 234");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentNum_Int());
    ASTCloneIntOptional2 astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentNum_Int());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestIntOptional2Absent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentNum_Int());
    ASTCloneIntOptional2 astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentNum_Int());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get().isPresentCloneEnum());
    ASTCloneEnumOptional astClone = ast.get().deepClone();
    Assertions.assertTrue(astClone.isPresentCloneEnum());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void TestEnumOptionalAbsent() throws IOException {
    DeepCloneParser parser = new DeepCloneParser();
    Optional<ASTCloneEnumOptional> ast = parser.parse_StringCloneEnumOptional("opt");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(ast.get().isPresentCloneEnum());
    ASTCloneEnumOptional astClone = ast.get().deepClone();
    Assertions.assertFalse(astClone.isPresentCloneEnum());
    Assertions.assertTrue(ast.get().deepEquals(astClone));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
