/* (c) https://github.com/MontiCore/monticore */

package mc.feature.deepclone;

import mc.feature.deepclone.deepclone._ast.*;
import mc.feature.deepclone.deepclone._parser.DeepCloneParser;
import mc.grammar.literals.ittestliterals._ast.ASTStringLiteral;
import mc.grammar.literals.ittestliterals._ast.ASTIntLiteral;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeepCloneNotEqualTest {

    @Test
    public void TestName() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneName> ast = parser.parse_StringCloneName("Name");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneName astClone = ast.get().deepClone();
        astClone.setName("NewName");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestNameList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneNameList> ast = parser.parse_StringCloneNameList("Name1 Name2 Name3");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneNameList astClone = ast.get().deepClone();
        astClone.setName(1, "NewName");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestNameOptionalPresent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneNameOptional> ast = parser.parse_StringCloneNameOptional("opt Name");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertTrue(ast.get().isPresentName());
        ASTCloneNameOptional astClone = ast.get().deepClone();
        astClone.setNameAbsent();
        assertFalse(astClone.isPresentName());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestNameOptionalAbsent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneNameOptional> ast = parser.parse_StringCloneNameOptional("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().isPresentName());
        ASTCloneNameOptional astClone = ast.get().deepClone();
        astClone.setName("NewName");
        assertTrue(astClone.isPresentName());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestAST() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneAST> ast = parser.parse_StringCloneAST("clone Name1 Name2 Name3");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneAST astClone = ast.get().deepClone();
        astClone.getCloneNameList().setName(1, "NewName");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestASTList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneASTList> ast = parser
                .parse_StringCloneASTList("clone Name1 Name2 clone Name3 Name4 Name5");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneASTList astClone = ast.get().deepClone();
        astClone.getCloneAST(1).getCloneNameList().setName(1, "NewName");
        assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setCloneASTAbsent();
        assertFalse(astClone.isPresentCloneAST());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestASTOptionalAbsent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneASTOptional> ast = parser.parse_StringCloneASTOptional("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().isPresentCloneAST());
        ASTCloneASTOptional astClone = ast.get().deepClone();
        ASTCloneAST newast = parser.parse_StringCloneAST("clone Name1 Name2").get();
        astClone.setCloneAST(newast);
        assertTrue(astClone.isPresentCloneAST());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestString() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneString> ast = parser.parse_StringCloneString("\"String\"");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneString astClone = ast.get().deepClone();

        ASTStringLiteral string = parser.parse_StringCloneString("\"NewString\"").get().getStringLiteral();
        astClone.setStringLiteral(string);
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestStringList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringList> ast = parser.parse_StringCloneStringList("\"String1\" \"String2\" \"String3\"");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneStringList astClone = ast.get().deepClone();
        ASTStringLiteral string = parser.parse_StringCloneString("\"NewString\"").get().getStringLiteral();
        astClone.getStringLiteralList().set(1, string);
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestStringOptionalPresent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringOptional> ast = parser.parse_StringCloneStringOptional("opt \"String\"");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertTrue(ast.get().isPresentStringLiteral());
        ASTCloneStringOptional astClone = ast.get().deepClone();
        astClone.setStringLiteralAbsent();
        assertFalse(astClone.isPresentStringLiteral());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestStringOptionalAbsent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringOptional> ast = parser.parse_StringCloneStringOptional("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().isPresentStringLiteral());
        ASTCloneStringOptional astClone = ast.get().deepClone();
        ASTStringLiteral string = parser.parse_StringCloneString("\"NewString\"").get().getStringLiteral();
        astClone.setStringLiteral(string);
        assertTrue(astClone.isPresentStringLiteral());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestInt() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneInt> ast = parser.parse_StringCloneInt("1234");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneInt astClone = ast.get().deepClone();
        ASTIntLiteral i= parser.parse_StringCloneInt("4567").get().getIntLiteral();
        astClone.setIntLiteral(i);
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestIntList() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntList> ast = parser.parse_StringCloneIntList("12 34 56");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneIntList astClone = ast.get().deepClone();
        ASTIntLiteral i= parser.parse_StringCloneInt("4567").get().getIntLiteral();
        astClone.setIntLiteral(1, i);
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestIntOptionalPresent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntOptional> ast = parser.parse_StringCloneIntOptional("opt 234");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertTrue(ast.get().isPresentIntLiteral());
        ASTCloneIntOptional astClone = ast.get().deepClone();
        astClone.setIntLiteralAbsent();
        assertFalse(astClone.isPresentIntLiteral());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestIntOptionalAbsent() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntOptional> ast = parser.parse_StringCloneIntOptional("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().isPresentIntLiteral());
        ASTCloneIntOptional astClone = ast.get().deepClone();
        ASTIntLiteral i= parser.parse_StringCloneInt("4567").get().getIntLiteral();
        astClone.setIntLiteral(i);
        assertTrue(astClone.isPresentIntLiteral());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestString2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneString2> ast = parser.parse_StringCloneString2("\"String\"");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneString2 astClone = ast.get().deepClone();
        astClone.setString("NewString");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestStringList2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringList2> ast = parser.parse_StringCloneStringList2("\"String1\" \"String2\" \"String3\"");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneStringList2 astClone = ast.get().deepClone();
        astClone.setString(1,"NewString");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestStringOptionalPresent2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringOptional2> ast = parser.parse_StringCloneStringOptional2("opt \"String\"");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertTrue(ast.get().isPresentString());
        ASTCloneStringOptional2 astClone = ast.get().deepClone();
        astClone.setStringAbsent();
        assertFalse(astClone.isPresentString());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestStringOptionalAbsent2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneStringOptional2> ast = parser.parse_StringCloneStringOptional2("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().isPresentString());
        ASTCloneStringOptional2 astClone = ast.get().deepClone();
        astClone.setString("NewString");
        assertTrue(astClone.isPresentString());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestInt2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneInt2> ast = parser.parse_StringCloneInt2("1234");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneInt2 astClone = ast.get().deepClone();
        astClone.setNum_Int("4567");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestIntList2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntList2> ast = parser.parse_StringCloneIntList2("12 34 56");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        ASTCloneIntList2 astClone = ast.get().deepClone();
        astClone.setNum_Int(1, "2345");
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestIntOptionalPresent2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt 234");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertTrue(ast.get().isPresentNum_Int());
        ASTCloneIntOptional2 astClone = ast.get().deepClone();
        astClone.setNum_IntAbsent();
        assertFalse(astClone.isPresentNum_Int());
        assertFalse(ast.get().deepEquals(astClone));
    }

    @Test
    public void TestIntOptionalAbsent2() throws IOException {
        DeepCloneParser parser = new DeepCloneParser();
        Optional<ASTCloneIntOptional2> ast = parser.parse_StringCloneIntOptional2("opt");
        assertFalse(parser.hasErrors());
        assertTrue(ast.isPresent());
        assertFalse(ast.get().isPresentNum_Int());
        ASTCloneIntOptional2 astClone = ast.get().deepClone();
        astClone.setNum_Int("1234");
        assertTrue(astClone.isPresentNum_Int());
        assertFalse(ast.get().deepEquals(astClone));
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
        astClone.setCloneEnumAbsent();
        assertFalse(astClone.isPresentCloneEnum());
        assertFalse(ast.get().deepEquals(astClone));
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
