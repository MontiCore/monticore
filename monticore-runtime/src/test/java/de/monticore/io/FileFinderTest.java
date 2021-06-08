/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io;


import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FileFinderTest {

  @BeforeClass
  public static void init(){
    Log.init();
    Log.enableFailQuick(false);
  }
  @Before
  public void setup(){
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
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(4, files.size());
    List<String> absolutePath = files.stream().map(File::getAbsolutePath).collect(Collectors.toList());
    assertTrue(absolutePath.contains(Paths.get("de", "monticore", "io", "Model1.cdsym").toFile().getAbsolutePath()));
    assertTrue(absolutePath.contains(Paths.get("de","monticore","io","Model1.cdsym").toFile().getAbsolutePath()));
    assertTrue(absolutePath.contains(Paths.get("de","monticore","io","Model1.dsym").toFile().getAbsolutePath()));
    assertTrue(absolutePath.contains(Paths.get("de","monticore","io","Model1.sdsym").toFile().getAbsolutePath()));

  }

  @Test
  public void testGetFiles2() {
    //getFiles() should find 1 Model, no regex.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(1, files.size());
    File f1 = files.get(0);
    assertTrue(f1.getAbsolutePath().endsWith(Paths.get("de","monticore","io","Model2.mc4").toString()));
  }

  @Test
  public void testGetFiles3() {
    // getFiles should find no Files.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model23";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(0, files.size());
  }

  @Test
  public void testGetFiles4() {
    //getFiles() should find 2 Models, using correct File extension.
    String fileExt = "cdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(2, files.size());
    File f1 = files.get(0);
    assertTrue(f1.getAbsolutePath().endsWith(Paths.get("de","monticore","io","Model1.cdsym").toString()));
    File f2 = files.get(1);
    assertTrue(f2.getAbsolutePath().endsWith(Paths.get("de","monticore","io","Model1.cdsym").toString()));
  }

  @Test
  public void testGetFiles5() {
    // getFiles() should not find any Models, using a Wrong File Extension
    String fileExt = "fdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(0, files.size());
  }

  @Test
  public void testGetFiles6(){
    //getFiles() should find 4 Models, using Regex.
    String fileExt = ".*dsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(4, files.size());
    List<String> absolutePath = files.stream().map(File::getAbsolutePath).collect(Collectors.toList());
    assertTrue(absolutePath.contains(Paths.get("de", "monticore", "io", "Model1.cdsym").toFile().getAbsolutePath()));
    assertTrue(absolutePath.contains(Paths.get("de","monticore","io","Model1.cdsym").toFile().getAbsolutePath()));
    assertTrue(absolutePath.contains(Paths.get("de","monticore","io","Model1.dsym").toFile().getAbsolutePath()));
    assertTrue(absolutePath.contains(Paths.get("de","monticore","io","Model1.sdsym").toFile().getAbsolutePath()));
  }

  @Test
  public void testGetFiles7() {
    // getFiles() should not find any Models, using a Wrong File Extension
    String fileExt = "xcdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(0, files.size());
  }

  @Test
  public void testGetFiles8() {
    // getFiles() should find 4 Models, using Regex
    String fileExt = "sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(0, files.size());
  }


  @Test
  public void testFindFiles1() {
    //findFiles() throws an Error because it finds more than 1 Model.
    String fileExt = ".*sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA1294"));
  }

  @Test
  public void testFindFiles2() {
    //findFiles() finds a Model, no previously loaded Files.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(1,list.size());
    assertEquals(Paths.get("de","monticore","io","Model2.mc4").toString(), list.get(0).getQualifiedPath().toString());
  }

  @Test
  public void testFindFiles3() {
    //findFiles() finds no Model because it's already loaded.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    loaded.add(Paths.get("de","monticore","io","Model2.mc4").toString());
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(0,list.size());
  }

  @Test
  public void testFindFiles4() {
    //findFiles() test with wrong Model name.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model22";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(0,list.size());
  }


  @Test
  public void testFindFiles5() {
    //fileFiles() finds 2 Models, with the same Name but in different ModelPath-Entries.
    String fileExt = ".*4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(2,list.size());
    assertEquals(Paths.get("de","monticore","io","Model2.cd4").toString(), list.get(0).getQualifiedPath().toString());
    assertEquals(Paths.get("de","monticore","io","Model2.mc4").toString(), list.get(1).getQualifiedPath().toString());
  }

  @Test
  public void testFindFile1() {
    //findFile() throws an Error because it expects 1 Models and finds 2 instead.
    String fileExt = ".*4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA7654"));
  }

  @Test
  public void testFindFile2() {
    //findFile() finds 1 Model.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertTrue(opt.isPresent());
    assertEquals(Paths.get("de","monticore","io","Model2.mc4").toString(), opt.get().getQualifiedPath().toString());
  }

  @Test
  public void testFindFile3() {
    //findFile does not find any Model because it's already loaded.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    loaded.add(Paths.get("de","monticore","io","Model2.mc4").toString());
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertFalse(opt.isPresent());
  }

  @Test
  public void testFindFile4() {
    //findFile() does not find any Model, Wrong Model name.
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model22";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src","test","models"));
    entries.add(Paths.get("src","test","resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertFalse(opt.isPresent());
  }
}

