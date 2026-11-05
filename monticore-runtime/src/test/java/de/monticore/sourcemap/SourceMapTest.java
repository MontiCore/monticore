/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SourceMapTest extends AbstractMCTest {

  @Test
  public void testDecodedSourceMapEquivalence() throws MalformedURLException {
    URL sourceUrl1 = new URL("file:/src/Main.java");
    URL sourceUrl2 = new URL("file:/src/Util.java");

    DecodedSource source1 = new DecodedSource(sourceUrl1, "class Main {}", false);
    DecodedSource source2 = new DecodedSource(sourceUrl2, "class Util {}", true);

    DecodedMapping mapping1 = new DecodedMapping(0, 0, source1, 0, 0, null);
    DecodedMapping mapping2 = new DecodedMapping(0, 10, source1, 0, 10, "foo");
    DecodedMapping mapping3 = new DecodedMapping(1, 0, source2, 5, 0, "bar");

    List<DecodedSource> sources = List.of(source1, source2);
    List<DecodedMapping> mappings = List.of(mapping1, mapping2, mapping3);

    DecodedSourceMap mapA = new DecodedSourceMap("Generated.java", sources, mappings, 0, 0);
    DecodedSourceMap mapB = new DecodedSourceMap("Generated.java", sources, mappings, 0, 0);

    assertNotSame(mapA, mapB);
    assertEquals(mapA, mapB);
    assertEquals(mapB, mapA);
    assertEquals(mapA.hashCode(), mapB.hashCode());
    assertEquals(mapA.toString(), mapB.toString());

    // Differences in individual fields must break equivalence
    assertNotEquals(mapA, new DecodedSourceMap("Other.java", sources, mappings, 0, 0));
    assertNotEquals(mapA, new DecodedSourceMap("Generated.java", List.of(source1), mappings, 0, 0));
    assertNotEquals(mapA, new DecodedSourceMap("Generated.java", sources, List.of(mapping1), 0, 0));
    assertNotEquals(mapA, new DecodedSourceMap("Generated.java", sources, mappings, 1, 0));
    assertNotEquals(mapA, new DecodedSourceMap("Generated.java", sources, mappings, 0, 1));
  }

  @Test
  public void testDecodedSourceEquivalence() throws MalformedURLException {
    URL url1 = new URL("file:/src/Test.java");
    URL url2 = new URL("file:/src/Test.java");
    URL urlOther = new URL("file:/src/Other.java");

    DecodedSource src1 = new DecodedSource(url1, "content", false);
    DecodedSource src2 = new DecodedSource(url2, "content", false);
    DecodedSource srcDifferentContent = new DecodedSource(url1, "different", false);
    DecodedSource srcIgnored = new DecodedSource(url1, "content", true);
    DecodedSource srcOtherUrl = new DecodedSource(urlOther, "content", false);

    assertNotSame(src1, src2);
    assertEquals(src1, src2);
    assertEquals(src1.hashCode(), src2.hashCode());
    assertNotEquals(src1, srcDifferentContent);
    assertNotEquals(src1, srcIgnored);
    assertNotEquals(src1, srcOtherUrl);
  }

  @Test
  public void testDecodedMappingEquivalence() throws MalformedURLException {
    URL url1 = new URL("file:/src/Test.java");
    URL url2 = new URL("file:/src/Test.java");
    URL urlOther = new URL("file:/src/Other.java");

    DecodedSource src1 = new DecodedSource(url1, "content", false);
    DecodedSource src2 = new DecodedSource(url2, "content", false);
    DecodedSource srcOtherUrl = new DecodedSource(urlOther, "content", false);

    DecodedMapping m1 = new DecodedMapping(0, 5, src1, 10, 2, "nameX");
    DecodedMapping m2 = new DecodedMapping(0, 5, src2, 10, 2, "nameX");
    DecodedMapping mDifferentCol = new DecodedMapping(0, 6, src1, 10, 2, "nameX");
    DecodedMapping mDifferentName = new DecodedMapping(0, 5, src1, 10, 2, "nameY");
    DecodedMapping mDifferentSrc = new DecodedMapping(0, 5, srcOtherUrl, 10, 2, "nameX");

    assertNotSame(m1, m2);
    assertEquals(m1, m2);
    assertEquals(m1.hashCode(), m2.hashCode());
    assertNotEquals(m1, mDifferentCol);
    assertNotEquals(m1, mDifferentName);
    assertNotEquals(m1, mDifferentSrc);
  }


  @Test
  public void testRoundTripEncodingAndDecoding() throws MalformedURLException {
    URL sourceUrl = new URL("file:/workspace/Original.java");
    DecodedSource source = new DecodedSource(sourceUrl, "public class Original {\n  void foo() {}\n}", false);

    DecodedMapping m1 = new DecodedMapping(0, 0, source, 0, 0, null);
    DecodedMapping m2 = new DecodedMapping(0, 7, source, 0, 7, null);
    DecodedMapping m3 = new DecodedMapping(1, 2, source, 1, 2, null);

    List<DecodedSource> sources = List.of(source);
    List<DecodedMapping> mappings = List.of(m1, m2, m3);

    DecodedSourceMap original = new DecodedSourceMap("Generated.java", sources, mappings);

    // Encode to JSON
    String json = Encoding.getEncodeSourceMap(original);
    assertNotNull(json);
    assertFalse(json.isEmpty());

    // Decode from JSON
    DecodedSourceMap decoded = Decoding.parseSourceMapJson(json, null);

    // Verify: different instance reference, but equivalent record state
    assertNotSame(original, decoded);
    assertEquals(original, decoded);
    assertEquals(original.hashCode(), decoded.hashCode());
    assertEquals(original.file(), decoded.file());
    assertEquals(original.sources(), decoded.sources());
    assertEquals(original.mappings(), decoded.mappings());
    assertEquals(original.lineOffSet(), decoded.lineOffSet());
    assertEquals(original.columnOffSet(), decoded.columnOffSet());

    MCAssertions.assertNoFindings();
  }

  /**
   * Verifies round-trip encoding and decoding with multiple sources,
   * symbol names, ignored sources, null content, and multi-line segments.
   */
  @Test
  public void testComprehensiveRoundTripWithNamesAndIgnoreList() throws MalformedURLException {
    URL src1Url = new URL("file:/app/ModelA.java");
    URL src2Url = new URL("file:/app/ModelB.java");

    DecodedSource src1 = new DecodedSource(src1Url, "class ModelA {}", false);
    DecodedSource src2 = new DecodedSource(src2Url, null, true); // ignored and null content

    DecodedMapping map1 = new DecodedMapping(0, 0, src1, 0, 0, "ModelA");
    DecodedMapping map2 = new DecodedMapping(0, 15, src1, 0, 12, null);
    DecodedMapping map3 = new DecodedMapping(1, 0, src2, 0, 0, "ModelB");
    DecodedMapping map4 = new DecodedMapping(1, 8, src2, 2, 4, "myField");

    List<DecodedSource> sources = List.of(src1, src2);
    List<DecodedMapping> mappings = List.of(map1, map2, map3, map4);

    DecodedSourceMap original = new DecodedSourceMap("Output.java", sources, mappings);

    String encoded = Encoding.getEncodeSourceMap(original);
    DecodedSourceMap decoded = Decoding.parseSourceMapJson(encoded, null);

    assertNotSame(original, decoded);
    assertEquals(original, decoded);
    assertEquals(original.hashCode(), decoded.hashCode());
    assertEquals(2, decoded.sources().size());
    assertEquals(4, decoded.mappings().size());
    assertTrue(decoded.sources().get(1).ignored());
    assertNull(decoded.sources().get(1).content());
    assertEquals("myField", decoded.mappings().get(3).name());

    MCAssertions.assertNoFindings();
  }
}
