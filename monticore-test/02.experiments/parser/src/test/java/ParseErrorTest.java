/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import parseerrors.ParseErrorsMill;
import parseerrors._parser.ParseErrorsParser;

import java.io.IOException;

/**
 * This test ensures
 * <br/>
 * Note: The concrete error messages are subject to change any may be changed
 */
public class ParseErrorTest {
  @BeforeAll
  public static void beforeClass() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  ParseErrorsParser parser;

  @BeforeEach
  public void beforeEach() {
    Log.clearFindings();
    ParseErrorsMill.init();
    parser = ParseErrorsMill.parser();
  }

  @Test
  public void TestKeyword() throws IOException {
    // A keyword is used at a location, where we expect a Name
    parser.parse_StringTestKeyword("keyword testkeyword");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched keyword 'keyword', expecting Name (found: KEYWORD3480559081) in rule stack: [TestKeyword]\u00A0\n" +
            "keyword testkeyword\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeywordPlus() throws IOException {
    // A keyword is used at a location, where we expect a Name& (plus keywords)
    parser.parse_StringTestKeywordPlus("keyword");
    Assertions.assertFalse(parser.hasErrors());
  }

  @Test
  public void TestNP() throws IOException {
    // A keyword is used at a location, where we expect a Name (wrapped in a nonterminal reference)
    parser.parse_StringTestNP("keyword testkeyword");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched keyword 'keyword', expecting Name (found: KEYWORD3480559081) in rule stack: [TestNP, TestKeyword]\u00A0\n" +
            "keyword testkeyword\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstant1Incorrect() throws IOException {
    // An incorrect name is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstant1("incorrect abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("missing 'keyconst1' at 'incorrect' in rule stack: [TestKeyConstant1]\u00A0\n" +
            "incorrect abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstant1Keyword() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstant1("keyword abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'keyword', expecting 'keyconst1' (found: KEYWORD3480559081) in rule stack: [TestKeyConstant1]\u00A0\n" +
            "keyword abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstant1KeywordInName() throws IOException {
    // A keyword is used at a location, where we expect a Name, right after a key-constant
    parser.parse_StringTestKeyConstant1("keyconst1 keyword");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched keyword 'keyword', expecting Name (found: KEYWORD3480559081) in rule stack: [TestKeyConstant1]\u00A0\n" +
                                    "keyconst1 keyword\n" +
                                    "          ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt1Incorrect() throws IOException {
    // An incorrect name is used at a location, where we expect a key-constant (Name with semantic predicate) (within an alt)
    parser.parse_StringTestKeyConstantAlt1("incorrect abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'incorrect', expecting 'keyconst2', 'keyconst1' (found: Name) in rule stack: [TestKeyConstantAlt1]\u00A0\n" +
            "incorrect abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt1Keyword() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate) (within an alt)
    parser.parse_StringTestKeyConstantAlt1("keyword abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'keyword', expecting 'keyconst2', 'keyconst1' (found: KEYWORD3480559081) in rule stack: [TestKeyConstantAlt1]\u00A0\n" +
            "keyword abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt2Incorrect() throws IOException {
    // An incorrect name is used at a location, where we expect a key-constant (Name with semantic predicate) (within a direct alt)
    parser.parse_StringTestKeyConstantAlt2("incorrect abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'incorrect', expecting 'keyconst2', 'keyconst1' (found: Name) in rule stack: [TestKeyConstantAlt2]\u00A0\n" +
            "incorrect abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt2Keyword() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate) (within a direct alt)
    parser.parse_StringTestKeyConstantAlt2("keyword abc");

    // In case of single-token deletion error recovery, the correct expected no-keywords should also be reported
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'keyword', expecting 'keyconst2', 'keyconst1' (found: KEYWORD3480559081) in rule stack: [TestKeyConstantAlt2]\u00A0\n" +
            "keyword abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestNoKeyWAlt1() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstantAlt1("notakeywordInvalid 1");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'notakeywordInvalid', expecting 'keyconst2', 'keyconst1' (found: Name) in rule stack: [TestKeyConstantAlt1]\u00A0\n" +
            "notakeywordInvalid 1\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestNoKeyWAlt2() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstantAlt2("notakeywordInvalid 1");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'notakeywordInvalid', expecting 'keyconst2', 'keyconst1' (found: Name) in rule stack: [TestKeyConstantAlt2]\u00A0\n" +
            "notakeywordInvalid 1\n" +
            "^", Log.getFindings().get(0).getMsg());
  }


  @Test
  public void testSepListDot() throws IOException {
    // Wrong separator used (dot instead of comma)
    parser.parse_StringTestSepList("seplist a.b");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input '.', expected EOF (found: POINT) in rule stack: [TestSepList]\u00A0\n" +
                                    "seplist a.b\n" +
                                    "         ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testSepListNone() throws IOException {
    // No separator used
    parser.parse_StringTestSepList("seplist a b");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'b', expected EOF (found: Name) in rule stack: [TestSepList]\u00A0\n" +
                                    "seplist a b\n" +
                                    "          ^", Log.getFindings().get(0).getMsg());
  }


  @Test
  public void testSepListHash() throws IOException {
    // An unknown token is used
    parser.parse_StringTestSepList("seplist a#b");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("token recognition error at: '#'\u00A0\n" +
            "seplist a#b\n" +
            "         ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testComp() throws IOException {
    // The KeyConstant does not match "component"
    parser.parse_StringComp("componentx MyName [ ]");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("missing 'component' at 'componentx' in rule stack: [Comp]\u00A0\n" +
            "componentx MyName [ ]\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCompInvalidKey() throws IOException {
    // The KeyConstant does not match ICompKeyInvalid (an empty string is also allowed)
    parser.parse_StringComp("component MyName { \n  ICompKeyInvalid \n }");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("extraneous input 'ICompKeyInvalid' expecting {'ICompKey', 'ICompKW', '}'} in rule stack: [Comp]\u00A0\n" +
            "  ICompKeyInvalid \n" +
            "  ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCompInvalidKeyPlus() throws IOException {
    // The KeyConstant does not match ICompKeyInvalid
    parser.parse_StringCompPlus("component MyName { \n  ICompKeyInvalid \n }");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'ICompKeyInvalid', expecting 'ICompKW', 'ICompKey' (found: Name) in rule stack: [CompPlus]\u00A0\n" +
            "  ICompKeyInvalid \n" +
            "  ^", Log.getFindings().get(0).getMsg());
  }


  @Test
  public void testComp3() throws IOException {
    // An unknown token is used
    parser.parse_StringComp("component MyName [ \n  \n ]");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("token recognition error at: '['\u00A0\n" +
            "component MyName [ \n" +
            "                 ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testComp4() throws IOException {
    // An unknown token is used
    parser.parse_StringComp("component MyName { \n # \n }");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("token recognition error at: '#'\u00A0\n" +
            " # \n" +
            " ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testUnknownAlts() throws IOException {
    // An incorrect input is used in an alt-context
    parser.parse_StringUnknownAlts("X");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'X', expecting 'UnknownAltsKey', 'UnknownAltsT' or Name in rule stack: [UnknownAlts]\u00A0\n" +
            "X\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testNoSpace() throws IOException {
    // NoSpace failed
    parser.parse_StringNoSpaceTest("@ Test");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("rule noSpaceTest failed predicate: {noSpace(2)}? in rule stack: [NoSpaceTest]\u00A0\n" +
            "@ Test\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testEmptyKeyConstant() throws IOException {
    // No input was provided for a KeyConstant
    parser.parse_StringComp("");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input '<EOF>', expecting 'component' (found: EOF) in rule stack: [Comp]\u00A0\n" +
            "\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testEmptyName() throws IOException {
    // No input was provided
    parser.parse_StringTestKeyword("");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched keyword '<EOF>', expecting Name (found: EOF) in rule stack: [TestKeyword]\u00A0\n" +
            "\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCDClass() throws IOException {
    // No input was provided
    parser.parse_StringCDLike("cd cl");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'cl', expected EOF (found: Name) in rule stack: [CDLike]\u00A0\n" +
                                    "cd cl\n" +
                                    "   ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCDPub() throws IOException {
    // Early EOF => unable to recover expected tokens
    parser.parse_StringCDLike("cd publ");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'publ', expected EOF (found: Name) in rule stack: [CDLike]\u00A0\n" +
                                    "cd publ\n" +
                                    "   ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCDPack() throws IOException {
    // No input was provided
    parser.parse_StringCDLike("cd package");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched keyword '<EOF>', expecting Name (found: EOF) in rule stack: [CDLike, CDLikeElement, CDLikeKey]\u00A0\n" +
                                    "cd package\n" +
                                    "          ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCDAssoc() throws IOException {
    // Incorrect/wrong "keyword", but EOF was possible => We are unable to recover the other expected tokens
    parser.parse_StringCDLike("cd \n class C1{}\n xxx\n association A1;");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'xxx', expected EOF (found: Name) in rule stack: [CDLike]\u00A0\n" +
                                    " xxx\n" +
                                    " ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCDAssocB() throws IOException {
    // Incorrect/wrong "keyword", EOF was not possible due to { }
    parser.parse_StringCDLikeB("cd { \n class C1{}\n xxx\n association A1; \n }");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("extraneous input 'xxx' expecting {'<<', 'private', 'association', 'public', 'composition', 'class', 'package', '+', '}'} in rule stack: [CDLikeB]\u00A0\n" +
                                    " xxx\n" +
                                    " ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testP() throws IOException {
    parser.parse_StringP("KeyWord abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'KeyWord', expecting 'ax', 'bx' (found: Name) in rule stack: [P]\u00A0\n" +
        "KeyWord abc" + "\n" +
        "^", Log.getFindings().get(0).getMsg());
  }
}
