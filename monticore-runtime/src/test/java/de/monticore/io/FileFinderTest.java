/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io;


import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileFinderTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    Log.clearFindings();
  }

  @Test
  public void testGetFiles1() {
    // getFiles() should find 4 Models, using Regex
    String fileExt = ".*sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    List<URL> files = find(mp, qualifiedModelName, fileExt);
    Assertions.assertEquals(4, files.size());
    List<String> absolutePath = files.stream().map(f ->f.toString()).collect(Collectors.toList());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFiles2() {
    //getFiles() should find 1 Model, no regex.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src", "test", "models"));
    entries.add(Paths.get("src", "test", "resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> file = mp.find(qualifiedModelName, fileExt);
    Assertions.assertTrue(file.isPresent());
    Assertions.assertTrue(file.get().toString().endsWith("de/monticore/io/Model2.mc4"));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFiles3() {
    // getFiles should find no Files.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model23";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src", "test", "models"));
    entries.add(Paths.get("src", "test", "resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> url = mp.find(qualifiedModelName, fileExt);
    assertFalse(url.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private void assertFalse(boolean present) {
  }

  @Test
  public void testGetFiles4() {
    //getFiles() should find 2 Models, using correct File extension.
    String fileExt = "cdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    List<URL> files = find(mp, qualifiedModelName, fileExt);
    Assertions.assertEquals(2, files.size());
    List<String> absolutePath = files.stream().map(f ->f.toString()).collect(Collectors.toList());
    Assertions.assertTrue(absolutePath.get(0).endsWith("de/monticore/io/Model1.cdsym"));
    Assertions.assertTrue(absolutePath.get(1).endsWith("de/monticore/io/Model1.cdsym"));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFiles5() {
    // getFiles() should not find any Models, using a Wrong File Extension
    String fileExt = "fdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src", "test", "models"));
    entries.add(Paths.get("src", "test", "resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> url = mp.find(qualifiedModelName, fileExt);
    assertFalse(url.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFiles6(){
    //getFiles() should find 4 Models, using Regex.
    String fileExt = ".*dsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    List<URL> files = find(mp, qualifiedModelName, fileExt);
    Assertions.assertEquals(4, files.size());
    List<String> absolutePath = files.stream().map(f ->f.toString()).collect(Collectors.toList());
    Assertions.assertTrue(Log.getFindings().isEmpty());
   }

  @Test
  public void testGetFiles7() {
    // getFiles() should not find any Models, using a Wrong File Extension
    String fileExt = "xcdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src", "test", "models"));
    entries.add(Paths.get("src", "test", "resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> url = mp.find(qualifiedModelName, fileExt);
    assertFalse(url.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetFiles8() {
    // getFiles() should find 0 Models, using Regex
    String fileExt = "sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    List<URL> files = find(mp, qualifiedModelName, fileExt);
    Assertions.assertEquals(0, files.size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testFindFiles1() {
    //findFiles() throws an Error because it finds more than 1 Model.
    String fileExt = ".*sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA1294"));

  }

  @Test
  public void testFindFiles2() {
    //findFiles() finds a Model, no previously loaded Files.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    Assertions.assertTrue(files.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFindFiles3() {
    //findFiles() finds no Model because it's already loaded.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    assertFalse(files.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFindFiles4() {
    //findFiles() test with wrong Model name.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model22";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    assertFalse(files.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testFindFiles5() {
    //fileFiles() finds 2 Models, with the same Name but in different ModelPath-Entries.
    String fileExt = ".*4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    List<URL> files = find(mp, qualifiedModelName, fileExt);
    Assertions.assertEquals(2, files.size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFindFile1() {
    //findFile() throws an Error because it expects 1 Models and finds 2 instead.
    String fileExt = ".*4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA1294"));
  }

  @Test
  public void testFindFile2() {
    //findFile() finds 1 Model.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    Assertions.assertTrue(files.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFindFile3() {
    //findFile does not find any Model because it's already loaded.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    assertFalse(files.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFindFile4() {
    //findFile() does not find any Model, Wrong Model name.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model22";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    MCPath mp = new MCPath(entries);
    Optional<URL> files = mp.find(qualifiedModelName, fileExt);
    assertFalse(files.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  public List<URL> find(MCPath mp, String qualifiedName, String fileExtRegEx) {
    // calculate the folderPath (e.g., "foo/bar") and fileNameRegEx (e.g., "Car.*sym")
    String folderPath = Names.getPathFromQualifiedName(qualifiedName);
    String fileNameRegEx = Names.getSimpleName(qualifiedName) + "\\." + fileExtRegEx;

    // initialize a file filter filtering for the regular expression
    FileFilter filter = new RegexFileFilter(fileNameRegEx);

    List<URL> resolvedURLs = new ArrayList<>();
    for (Path p : mp.getEntries()) {
      File folder = p.resolve(folderPath).toFile(); //e.g., "src/test/resources/foo/bar"
      if (folder.exists() && folder.isDirectory()) {
        // perform the actual file filter on the folder and collect result
        Arrays.stream(folder.listFiles(filter))
                .map(f -> mp.toURL(folder.toPath().resolve(f.getName())))
                .filter(Optional::isPresent)
                .forEach(f -> resolvedURLs.add(f.get()));
      }
    }
    return resolvedURLs;
  }
}

