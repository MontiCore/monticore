/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    Assertions.assertEquals("mismatched input 'incorrect', expecting 'keyconst1' in rule stack: [TestKeyConstant1, Nokeyword_keyconst1_2760036429]\u00A0\n" +
            "incorrect abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstant1Keyword() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstant1("keyword abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("mismatched input 'keyword', expecting 'keyconst1' in rule stack: [TestKeyConstant1, Nokeyword_keyconst1_2760036429]\u00A0\n" +
            "keyword abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt1Incorrect() throws IOException {
    // An incorrect name is used at a location, where we expect a key-constant (Name with semantic predicate) (within an alt)
    parser.parse_StringTestKeyConstantAlt1("incorrect abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'incorrect', expecting 'keyconst1' or 'keyconst2' in rule stack: [TestKeyConstantAlt1]\u00A0\n" +
            "incorrect abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt1Keyword() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate) (within an alt)
    parser.parse_StringTestKeyConstantAlt1("keyword abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("unexpected keyword 'keyword', expecting Name in rule stack: [TestKeyConstantAlt1]\u00A0\n" +
            "keyword abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt2Incorrect() throws IOException {
    // An incorrect name is used at a location, where we expect a key-constant (Name with semantic predicate) (within a direct alt)
    parser.parse_StringTestKeyConstantAlt2("incorrect abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'incorrect', expecting 'keyconst1' or 'keyconst2' in rule stack: [TestKeyConstantAlt2]\u00A0\n" +
            "incorrect abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestKeyConstantAlt2Keyword() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate) (within a direct alt)
    parser.parse_StringTestKeyConstantAlt2("keyword abc");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("unexpected keyword 'keyword', expecting Name in rule stack: [TestKeyConstantAlt2]\u00A0\n" +
            "keyword abc\n" +
            "^", Log.getFindings().get(0).getMsg());
  }


  @Test
  public void TestNoKeyWAlt1() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstantAlt1("notakeywordInvalid 1");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'notakeywordInvalid1', expecting 'keyconst1' or 'keyconst2' in rule stack: [TestKeyConstantAlt1]\u00A0\n" +
            "notakeywordInvalid 1\n" +
            "                   ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void TestNoKeyWAlt2() throws IOException {
    // A keyword is used at a location, where we expect a key-constant (Name with semantic predicate)
    parser.parse_StringTestKeyConstantAlt2("notakeywordInvalid 1");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'notakeywordInvalid1', expecting 'keyconst1' or 'keyconst2' in rule stack: [TestKeyConstantAlt2]\u00A0\n" +
            "notakeywordInvalid 1\n" +
            "                   ^", Log.getFindings().get(0).getMsg());
  }


  @Test
  public void testSepListDot() throws IOException {
    // Wrong separator used (dot instead of comma)
    parser.parse_StringTestSepList("seplist a.b");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("Expected EOF but found token [@2,9:9='.',<10>,1:9]", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testSepListNone() throws IOException {
    // No separator used
    parser.parse_StringTestSepList("seplist a b");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("Expected EOF but found token [@2,10:10='b',<16>,1:10]", Log.getFindings().get(0).getMsg());
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
    Assertions.assertEquals("mismatched input 'componentx', expecting 'component' in rule stack: [Comp, Nokeyword_component_2895060221]\u00A0\n" +
            "componentx MyName [ ]\n" +
            "^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCompInvalidKey() throws IOException {
    // The KeyConstant does not match ICompKeyInvalid (an empty string is also allowed)
    parser.parse_StringComp("component MyName { \n  ICompKeyInvalid \n }");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'ICompKeyInvalid', expecting 'ICompKW' or 'ICompKey' or '}' in rule stack: [Comp]\u00A0\n" +
            "  ICompKeyInvalid \n" +
            "  ^", Log.getFindings().get(0).getMsg());
  }

  @Test
  public void testCompInvalidKeyPlus() throws IOException {
    // The KeyConstant does not match ICompKeyInvalid
    parser.parse_StringCompPlus("component MyName { \n  ICompKeyInvalid \n }");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertEquals("no viable alternative at input 'ICompKeyInvalid', expecting 'ICompKW' or 'ICompKey' in rule stack: [CompPlus, IComp]\u00A0\n" +
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
    Assertions.assertEquals("no viable alternative at input 'X', expecting 'UnknownAltsKey' or 'UnknownAltsT' or Name (with additional constraints from unknownAlts) in rule stack: [UnknownAlts]\u00A0\n" +
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
    Assertions.assertEquals("mismatched input '<EOF>', expecting 'component' in rule stack: [Comp, Nokeyword_component_2895060221]\u00A0\n" +
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


}
