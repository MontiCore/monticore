/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype;

import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testmccommon.TestMCCommonMill;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(TestMCCommonMill.class)
public class StereoValueContentTest {

  @Test
  public void testSimpleBuilder() {
    String input = "Hello world";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setContent(input).build();
    assertEquals(input, sv.getContent());
  }

  @Test
  public void testSimple() {
    String input = "Hello world";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo").build();
    sv.setContent(input);
    assertEquals(input, sv.getContent());
  }

  @Test
  public void testQuotationBuilder() {
    String input = "Hello \"world\"";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setContent(input).build();
    assertEquals(input, sv.getContent());
  }

  @Test
  public void testQuotation() {
    String input = "Hello \"world\"";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo").build();
    sv.setContent(input);
    assertEquals(input, sv.getContent());
  }

  @Test
  public void testBackslashBuilder() {
    String input = "Hello \\ world,\n hello\\people";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setContent(input).build();
    assertEquals(input, sv.getContent());
  }

  @Test
  public void testBackslash() {
    String input = "Hello \\ world,\n hello\\people";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo").build();
    sv.setContent(input);
    assertEquals(input, sv.getContent());
  }


  @Test
  public void testSetTextBuilder() {
    String input = "Hello \\ \"world\", hello\\people";
    var sv = TestMCCommonMill.stereoValueBuilder().setName("Stereo")
            .setText(MCCommonLiteralsMill.stringLiteralBuilder()
                    .setSource(StringEscapeUtils.escapeJava(input)).build())
            .build();
    assertEquals(input, sv.getContent());
  }

}
