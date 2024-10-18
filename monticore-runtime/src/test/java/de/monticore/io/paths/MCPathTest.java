/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io.paths;

import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class MCPathTest {

  @BeforeEach
  public void setup(){
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
    Assertions.assertTrue(logback.isPresent());
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertTrue(fileInJar.isPresent());
    Assertions.assertFalse(nonExistent.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
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

    Assertions.assertTrue(logback.isPresent());
    Assertions.assertTrue(grammar.isPresent());
    Assertions.assertFalse(nonExistent.isPresent());

    Assertions.assertTrue(logback2.isPresent());
    Assertions.assertTrue(grammar2.isPresent());
    Assertions.assertFalse(nonExistent2.isPresent());

    mp.addEntry(Paths.get("src/test/resources/jar/Test.jar"));
    Optional<URL> fileInJar = mp.find("de.monticore.MCBasics", "mc4");
    Optional<URL> fileInJar2 = mp.find("de.monticore.MCBasics", "m.4");
    Optional<URL> fileNotInJar = mp.find("MCBasics", "m.4");
    Assertions.assertTrue(fileInJar.isPresent());
    Assertions.assertTrue(fileInJar2.isPresent());
    Assertions.assertFalse(fileNotInJar.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddEntry(){
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    Path models = Paths.get("src/test/models");
    mp.addEntry(resources);
    Assertions.assertEquals(1, mp.getEntries().size());
    mp.addEntry(models);
    Assertions.assertEquals(2, mp.getEntries().size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRemoveEntry(){
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    mp.addEntry(resources);
    Assertions.assertEquals(1, mp.getEntries().size());
    mp.removeEntry(resources);
    Assertions.assertTrue(mp.isEmpty());
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertEquals(3, paths.size());
    Assertions.assertTrue(paths.contains(resources.toAbsolutePath()));
    Assertions.assertTrue(paths.contains(models.toAbsolutePath()));
    Assertions.assertTrue(paths.contains(java.toAbsolutePath()));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsEmpty(){
    MCPath mp = new MCPath();
    Assertions.assertTrue(mp.isEmpty());
    Path resources = Paths.get("src/test/resources");
    mp.addEntry(resources);
    Assertions.assertFalse(mp.isEmpty());
    mp.removeEntry(resources);
    Assertions.assertTrue(mp.isEmpty());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testToString() throws MalformedURLException {
    MCPath mp = new MCPath();
    Path resources = Paths.get("src/test/resources");
    Path models = Paths.get("src/test/models");
    mp.addEntry(resources);
    mp.addEntry(models);
    Assertions.assertEquals("[" + resources.toUri().toURL().toString() + ", "
      + models.toUri().toURL().toString() + "]", mp.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testToPath() throws MalformedURLException {
    Path resources = Paths.get("src/test/resources");
    URL url = resources.toUri().toURL();
    Optional<Path> result = MCPath.toPath(url);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(result.get(), resources.toAbsolutePath());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testToURL() throws URISyntaxException {
    Path resources = Paths.get("src/test/resources");
    Optional<URL> result = MCPath.toURL(resources);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(result.get().toURI(), resources.toUri());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testReportAmbiguity() throws MalformedURLException {
    List<URL> urlList = new ArrayList<>();
    URL resources = Paths.get("src/test/resources").toUri().toURL();
    urlList.add(resources);
    urlList.add(resources);
    MCPath.reportAmbiguity(urlList, "src/test/resources");
    List<Finding> findings = Log.getFindings().stream().filter(f -> f.getMsg().startsWith("0xA1294")).collect(Collectors.toList());
    Assertions.assertEquals(1, findings.size());
    Assertions.assertEquals("0xA1294 The following entries for the file `" + "src/test/resources" + "` are ambiguous:"
      + "\n" + "{" + resources.toString() + ",\n" + resources.toString() + "}", findings.get(0).getMsg());
  }
  @Test
  @Disabled("$JAVA_HOME must be set & might be an arbitrary version (e.g. intelliJ can use its own JDK)." +
      "Path to <rt.jar> might differ between java version")
  public void testShouldFind(){
    String jdk = System.getenv("JAVA_HOME").replaceAll("\\\\", "/") + "/jre/lib/rt.jar";
    MCPath mp = new MCPath(jdk);
    Assertions.assertTrue(mp.find("java/util/List.class").isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testShouldNotFind(){
    MCPath mp = new MCPath("");
    Assertions.assertFalse(mp.find("java/util/List.class").isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testShouldNotFind2(){
    MCPath mp = new MCPath("this/is/a/test");
    Assertions.assertFalse(mp.find("java/util/List.class").isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCachedAmbiguous() {
    // Test if the mcpath-caches are invalidated when adding new entries
    Log.clearFindings();
    MCPath path = new MCPath();
    path.addEntry(Paths.get("src/test/resources/paths/1/a"));
    Assertions.assertTrue(path.find("AFile", "txt").isPresent());
    Assertions.assertEquals(0, Log.getErrorCount());
    path.addEntry(Paths.get("src/test/resources/paths/2/a"));
    Assertions.assertTrue(path.find("AFile", "txt").isEmpty());
    List<Finding> findings = Log.getFindings().stream().filter(f -> f.getMsg().startsWith("0xA1294")).collect(Collectors.toList());
    Assertions.assertEquals(1, findings.size());
    Assertions.assertEquals("0xA1294 The following entries for the file `" + "AFile\\.txt" + "` are ambiguous:"
        + "\n" + "{" + Paths.get("src/test/resources/paths/1/a/AFile.txt").toUri().toString().replaceAll("///","/") + ",\n"
        + Paths.get("src/test/resources/paths/2/a/AFile.txt").toUri().toString().replaceAll("///","/") + "}", findings.get(0).getMsg());
  }

  @Test
  public void testCachedSym() throws IOException {
    // Test if the mcpath-jar-cache respects removal
    Log.clearFindings();
    File tempF = Files.createTempDir();
    tempF.mkdirs();
    File jar = new File(tempF, "test.jar");

    FileOutputStream fout = new FileOutputStream(jar);
    JarOutputStream jarOut = new JarOutputStream(fout);
    jarOut.putNextEntry(new ZipEntry("de/mc/")); // Folders must end with "/".
    jarOut.putNextEntry(new ZipEntry("de/mc/A.test.sym"));
    jarOut.write("{}".getBytes());
    jarOut.closeEntry();

    jarOut.close();
    fout.close();

    MCPath path = new MCPath();
    path.addEntry(jar.toPath());

    Assertions.assertTrue(path.find("de.mc.A", ".*sym").isPresent());
    path.removeEntry(jar.toPath());
    Assertions.assertFalse(path.find("de.mc.A", ".*sym").isPresent(), "removeEntry was not completed");
  }

  
}
