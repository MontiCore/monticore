/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RegExTypeTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAst_testRegExs1() throws IOException {
    checkTypeRoundTrip("R\"hello\"");
    checkTypeRoundTrip("R\"a|b\"");
    checkTypeRoundTrip("R\"a[b]\"");
    checkTypeRoundTrip("R\"a[bcd]e\"");
    checkTypeRoundTrip("R\"a[b.c]\"");
    checkTypeRoundTrip("R\"a[b-c]\"");
    checkTypeRoundTrip("R\"a(b|c)\"");
    checkTypeRoundTrip("R\"a(?:bcd)e\"");
    checkTypeRoundTrip("R\"a\\9b\"");
    checkTypeRoundTrip("R\"abZ\"");
    checkTypeRoundTrip("R\"...\"");
    checkTypeRoundTrip("R\"0129\"");
    checkTypeRoundTrip("R\"a^(b|c)d\"");
    checkTypeRoundTrip("R\"a(bc)*d\"");
    checkTypeRoundTrip("R\"a(b|c){2,33}d\"");
    checkTypeRoundTrip("R\"a(bc){33}d\"");
    checkTypeRoundTrip("R\"a\\p{TestCharSet}b\"");
    checkTypeRoundTrip("R\"a\\w\\Bb\"");
  }

  @Test
  public void symTypeFromAst_testRegExs2() throws IOException {
    // tests (mostly) the examples from the documentation
    checkTypeRoundTrip("R\"hello\"");
    checkTypeRoundTrip("R\"gray|grey\"");
    checkTypeRoundTrip("R\"gr(a|e)y\"");
    checkTypeRoundTrip("R\"gr[ae]y\"");
    checkTypeRoundTrip("R\"b[ae$iou]bble\"");
    checkTypeRoundTrip("R\"[b-chm-pP]at|ot\"");
    checkTypeRoundTrip("R\"colou?r\"");
    checkTypeRoundTrip("R\"rege(x(es)?|xps?)\"");
    checkTypeRoundTrip("R\"go*gle\"");
    checkTypeRoundTrip("R\"go+gle\"");
    checkTypeRoundTrip("R\"g(oog)+le\"");
    checkTypeRoundTrip("R\"z{3}\"");
    checkTypeRoundTrip("R\"z{3,6}\"");
    checkTypeRoundTrip("R\"z{3,}\"");
    checkTypeRoundTrip("R\"[Bb]rainch\\*\\*k\"");
    checkTypeRoundTrip("R\"\\d\"");
    checkTypeRoundTrip("R\"\\d{5}(-\\d{4})?\"");
    checkTypeRoundTrip("R\"1\\d{10}\"");
    checkTypeRoundTrip("R\"[2-9]|[12]\\d|3[0-6]\"");
    checkTypeRoundTrip("R\"Hello\\nworld\"");
    checkTypeRoundTrip("R\"mi.....ft\"");
    checkTypeRoundTrip("R\"\\d+(\\.\\d\\d)?\"");
    checkTypeRoundTrip("R\"[^i*&2@]\"");
    checkTypeRoundTrip("R\"//[^\\r\\n]*[\\r\\n]\"");
    checkTypeRoundTrip("R\"^dog\"");
    checkTypeRoundTrip("R\"dog$\"");
    checkTypeRoundTrip("R\"^dog$\"");
    checkTypeRoundTrip("R\"test[-+*,^^?()|}{]\"");
    checkTypeRoundTrip("R\"(\\d\\d)\\1\"");
    checkTypeRoundTrip("R\"\\p{test}\"");
  }

}
