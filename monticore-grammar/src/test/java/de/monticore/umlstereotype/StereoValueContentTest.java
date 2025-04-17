/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype;

import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.testmccommon.TestMCCommonMill;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StereoValueContentTest {
  @BeforeEach
  public void init() {
    LogStub.initPlusLog();
    LogStub.clearFindings();
    TestMCCommonMill.init();
  }

  @AfterEach
  public void postCheck() {
    Assertions.assertTrue(LogStub.getFindings().isEmpty());
  }

  @Test
  public void testSimpleBuilder() {
    String input = "Hello world";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setContent(input).build();
    Assertions.assertEquals(input, sv.getContent());
  }

  @Test
  public void testSimple() {
    String input = "Hello world";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo").build();
    sv.setContent(input);
    Assertions.assertEquals(input, sv.getContent());
  }

  @Test
  public void testQuotationBuilder() {
    String input = "Hello \"world\"";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setContent(input).build();
    Assertions.assertEquals(input, sv.getContent());
  }

  @Test
  public void testQuotation() {
    String input = "Hello \"world\"";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo").build();
    sv.setContent(input);
    Assertions.assertEquals(input, sv.getContent());
  }

  @Test
  public void testBackslashBuilder() {
    String input = "Hello \\ world,\n hello\\people";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setContent(input).build();
    Assertions.assertEquals(input, sv.getContent());
  }

  @Test
  public void testBackslash() {
    String input = "Hello \\ world,\n hello\\people";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo").build();
    sv.setContent(input);
    Assertions.assertEquals(input, sv.getContent());
  }


  @Test
  public void testSetTextBuilder() {
    String input = "Hello \\ \"world\", hello\\people";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setText(MCCommonLiteralsMill.stringLiteralBuilder()
                    .setSource(StringEscapeUtils.escapeJava(input)).build())
            .build();
    Assertions.assertEquals(input, sv.getContent());
  }

}
