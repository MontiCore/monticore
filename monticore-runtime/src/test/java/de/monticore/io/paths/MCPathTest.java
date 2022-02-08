/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io.paths;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MCPathTest {

  @BeforeClass
  public static void setup(){
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testFindWithOneArgument(){
    MCPath mp = new MCPath();
    mp.addEntry(Paths.get("src/test/resources"));
    Optional<URL> logback = mp.find("logback.groovy");
    Optional<URL> grammar = mp.find("de/monticore/io/Model3.mc4");
    Optional<URL> nonExistent = mp.find("Test.mc4");

    mp.addEntry(Paths.get("src/test/resources/jar/Test.jar"));
    Optional<URL> fileInJar = mp.find("de/monticore/MCBasics.mc4");
    assertTrue(logback.isPresent());
    assertTrue(grammar.isPresent());
    assertTrue(fileInJar.isPresent());
    assertFalse(nonExistent.isPresent());
  }

  @Test
  public void testFindWithTwoArguments(){
    MCPath mp = new MCPath();
    mp.addEntry(Paths.get("src/test/resources"));
    Optional<URL> logback = mp.find("logback", "groovy");
    Optional<URL> grammar = mp.find("de.monticore.io.Model3", "mc4");
    Optional<URL> nonExistent = mp.find("Test", "mc4");
    Optional<URL> logback2 = mp.find("logback", "g.*");
    Optional<URL> grammar2 = mp.find("de.monticore.io.Model3", ".*4");
    Optional<URL> nonExistent2 = mp.find("Test", "m.4");

    assertTrue(logback.isPresent());
    assertTrue(grammar.isPresent());
    assertFalse(nonExistent.isPresent());

    assertTrue(logback2.isPresent());
    assertTrue(grammar2.isPresent());
    assertFalse(nonExistent2.isPresent());

    mp.addEntry(Paths.get("src/test/resources/jar/Test.jar"));
    Optional<URL> fileInJar = mp.find("de.monticore.MCBasics", "mc4");
    Optional<URL> fileInJar2 = mp.find("de.monticore.MCBasics", "m.4");
    Optional<URL> fileNotInJar = mp.find("MCBasics", "m.4");
    assertTrue(fileInJar.isPresent());
    assertTrue(fileInJar2.isPresent());
    assertFalse(fileNotInJar.isPresent());
  }

  @Test
  public void testAddEntry(){
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    Path models = Paths.get("src/test/models");
    mp.addEntry(resources);
    assertEquals(1, mp.getEntries().size());
    mp.addEntry(models);
    assertEquals(2, mp.getEntries().size());
  }

  @Test
  public void testRemoveEntry(){
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    mp.addEntry(resources);
    assertEquals(1, mp.getEntries().size());
    mp.removeEntry(resources);
    assertTrue(mp.isEmpty());
  }

  @Test
  public void testGetEntries(){
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    Path models = Paths.get("src/test/models");
    Path java = Paths.get("src/test/java");
    mp.addEntry(resources);
    mp.addEntry(models);
    mp.addEntry(java);
    Collection<Path> paths = mp.getEntries();
    assertEquals(3, paths.size());
    assertTrue(paths.contains(resources.toAbsolutePath()));
    assertTrue(paths.contains(models.toAbsolutePath()));
    assertTrue(paths.contains(java.toAbsolutePath()));
  }

  @Test
  public void testIsEmpty(){
    MCPath mp = new MCPath();
    assertTrue(mp.isEmpty());
    Path resources = Paths.get("src/test/resources");
    mp.addEntry(resources);
    assertFalse(mp.isEmpty());
    mp.removeEntry(resources);
    assertTrue(mp.isEmpty());
  }

  @Test
  public void testToString() throws MalformedURLException {
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    Path models = Paths.get("src/test/models");
    mp.addEntry(resources);
    mp.addEntry(models);
    assertEquals("[" + resources.toUri().toURL().toString() + ", "
      + models.toUri().toURL().toString() + "]" ,mp.toString());
  }

  @Test
  public void testToPath() throws MalformedURLException {
    Path resources = Paths.get("src/test/resources");
    URL url = resources.toUri().toURL();
    Optional<Path> result = MCPath.toPath(url);
    assertTrue(result.isPresent());
    assertEquals(result.get(), resources.toAbsolutePath());
  }

  @Test
  public void testToURL() throws URISyntaxException {
    Path resources = Paths.get("src/test/resources");
    Optional<URL> result = MCPath.toURL(resources);
    assertTrue(result.isPresent());
    assertEquals(result.get().toURI(), resources.toUri());
  }

  @Test
  public void testReportAmbiguity() throws MalformedURLException {
    List<URL> urlList = new ArrayList<>();
    URL resources = Paths.get("src/test/resources").toUri().toURL();
    urlList.add(resources);
    urlList.add(resources);
    MCPath.reportAmbiguity(urlList, "src/test/resources");
    List<Finding> findings = Log.getFindings().stream().filter(f -> f.getMsg().startsWith("0xA1294")).collect(Collectors.toList());
    assertEquals(1, findings.size());
    assertEquals("0xA1294 The following entries for the file `" + "src/test/resources" + "` are ambiguous:"
      + "\n" + "{" + resources.toString() + ",\n" + resources.toString() + "}", findings.get(0).getMsg());
  }
  @Test
  public void testShouldFind(){
    String jdk = System.getenv("JAVA_HOME").replaceAll("\\\\", "/") + "/jre/lib/rt.jar";
    MCPath mp = new MCPath(jdk);
    assertTrue(mp.find("java/util/List.class").isPresent());
  }

  @Test
  public void testShouldNotFind(){
    MCPath mp = new MCPath("");
    assertFalse(mp.find("java/util/List.class").isPresent());
  }

  @Test
  public void testShouldNotFind2(){
    MCPath mp = new MCPath("this/is/a/test");
    assertFalse(mp.find("java/util/List.class").isPresent());
  }
  
}
