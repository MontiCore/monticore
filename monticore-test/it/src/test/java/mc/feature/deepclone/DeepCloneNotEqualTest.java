/* (c) https://github.com/MontiCore/monticore */

package mc.feature.deepclone;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.deepclone.deepclone._ast.*;
import mc.feature.deepclone.deepclone._parser.DeepCloneParser;
import mc.grammar.literals.ittestliterals._ast.ASTStringLiteral;
import mc.grammar.literals.ittestliterals._ast.ASTIntLiteral;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeepCloneNotEqualTest {
    
    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
    }
    
    @Test
    public void TestName() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneName> ast = parser.parse_StringCloneName("Name");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneName astClone = ast.get().deepClone();
        astClone.setName("NewName");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestNameList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneNameList> ast = parser.parse_StringCloneNameList("Name1 Name2 Name3");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneNameList astClone = ast.get().deepClone();
        astClone.setName(1, "NewName");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setNameAbsent();
        Assertions.assertFalse(astClone.isPresentName());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setName("NewName");
        Assertions.assertTrue(astClone.isPresentName());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestAST() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneAST> ast = parser.parse_StringCloneAST("clone Name1 Name2 Name3");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneAST astClone = ast.get().deepClone();
        astClone.getCloneNameList().setName(1, "NewName");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.getCloneAST(1).getCloneNameList().setName(1, "NewName");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setCloneASTAbsent();
        Assertions.assertFalse(astClone.isPresentCloneAST());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        ASTCloneAST newast = parser.parse_StringCloneAST("clone Name1 Name2").get();
        astClone.setCloneAST(newast);
        Assertions.assertTrue(astClone.isPresentCloneAST());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestString() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneString> ast = parser.parse_StringCloneString("\"String\"");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneString astClone = ast.get().deepClone();

        ASTStringLiteral string = parser.parse_StringCloneString("\"NewString\"").get().getStringLiteral();
        astClone.setStringLiteral(string);
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestStringList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringList> ast = parser.parse_StringCloneStringList("\"String1\" \"String2\" \"String3\"");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneStringList astClone = ast.get().deepClone();
        ASTStringLiteral string = parser.parse_StringCloneString("\"NewString\"").get().getStringLiteral();
        astClone.getStringLiteralList().set(1, string);
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setStringLiteralAbsent();
        Assertions.assertFalse(astClone.isPresentStringLiteral());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        ASTStringLiteral string = parser.parse_StringCloneString("\"NewString\"").get().getStringLiteral();
        astClone.setStringLiteral(string);
        Assertions.assertTrue(astClone.isPresentStringLiteral());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestInt() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneInt> ast = parser.parse_StringCloneInt("1234");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneInt astClone = ast.get().deepClone();
        ASTIntLiteral i= parser.parse_StringCloneInt("4567").get().getIntLiteral();
        astClone.setIntLiteral(i);
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestIntList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntList> ast = parser.parse_StringCloneIntList("12 34 56");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneIntList astClone = ast.get().deepClone();
        ASTIntLiteral i= parser.parse_StringCloneInt("4567").get().getIntLiteral();
        astClone.setIntLiteral(1, i);
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setIntLiteralAbsent();
        Assertions.assertFalse(astClone.isPresentIntLiteral());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        ASTIntLiteral i= parser.parse_StringCloneInt("4567").get().getIntLiteral();
        astClone.setIntLiteral(i);
        Assertions.assertTrue(astClone.isPresentIntLiteral());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestString2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneString2> ast = parser.parse_StringCloneString2("\"String\"");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneString2 astClone = ast.get().deepClone();
        astClone.setString("NewString");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestStringList2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringList2> ast = parser.parse_StringCloneStringList2("\"String1\" \"String2\" \"String3\"");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneStringList2 astClone = ast.get().deepClone();
        astClone.setString(1,"NewString");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setStringAbsent();
        Assertions.assertFalse(astClone.isPresentString());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setString("NewString");
        Assertions.assertTrue(astClone.isPresentString());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestInt2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneInt2> ast = parser.parse_StringCloneInt2("1234");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneInt2 astClone = ast.get().deepClone();
        astClone.setNum_Int("4567");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestIntList2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntList2> ast = parser.parse_StringCloneIntList2("12 34 56");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        ASTCloneIntList2 astClone = ast.get().deepClone();
        astClone.setNum_Int(1, "2345");
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestIntOptionalPresent2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt 234");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        Assertions.assertTrue(ast.get().isPresentNum_Int());
        ASTCloneIntOptional2 astClone = ast.get().deepClone();
        astClone.setNum_IntAbsent();
        Assertions.assertFalse(astClone.isPresentNum_Int());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void TestIntOptionalAbsent2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt");
        Assertions.assertFalse(parser.hasErrors());
        Assertions.assertTrue(ast.isPresent());
        Assertions.assertFalse(ast.get().isPresentNum_Int());
        ASTCloneIntOptional2 astClone = ast.get().deepClone();
        astClone.setNum_Int("1234");
        Assertions.assertTrue(astClone.isPresentNum_Int());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setCloneEnumAbsent();
        Assertions.assertFalse(astClone.isPresentCloneEnum());
        Assertions.assertFalse(ast.get().deepEquals(astClone));
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

   /* @Test
    public void TestEnumOptionalAbsent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneEnumOptional> ast = parser.parse_StringCloneEnumOptional("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().getCloneEnumOpt().isPresent());
        ASTCloneEnumOptional astClone = ast.get().deepClone();
        ASTCloneEnum e = parser.parse_StringCloneEn("enum").get();
        astClone.setCloneEnumOpt(e);
        astClone.set
        assertFalse(astClone.getCloneEnumOpt().isPresent());
        assertTrue(ast.get().deepEquals(astClone));
    }*/
}
