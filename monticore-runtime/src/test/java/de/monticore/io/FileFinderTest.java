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
  public void getFilesShouldFindTwoModelsRegExtFileExt() {
    String fileExt = "*sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(2, files.size());
    File f1 = files.get(0);
    assertTrue(f1.getAbsolutePath().endsWith("de\\monticore\\io\\Model1.cdsym"));
    File f2 = files.get(1);
    assertTrue(f2.getAbsolutePath().endsWith("de\\monticore\\io\\Model1.cdsym"));
  }

  @Test
  public void getFilesShouldFindOneModel() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(1, files.size());
    File f1 = files.get(0);
    assertTrue(f1.getAbsolutePath().endsWith("de\\monticore\\io\\Model2.mc4"));
  }

  @Test
  public void getFilesShouldNotFindAnyModel() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model23";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(0, files.size());
  }

  @Test
  public void getFilesShouldFindTwoModelsCorrectFileExt() {
    String fileExt = "cdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(2, files.size());
    File f1 = files.get(0);
    assertTrue(f1.getAbsolutePath().endsWith("de\\monticore\\io\\Model1.cdsym"));
    File f2 = files.get(1);
    assertTrue(f2.getAbsolutePath().endsWith("de\\monticore\\io\\Model1.cdsym"));
  }

  @Test
  public void getFilesShouldNotFindAnyModelsWrongFileExt() {
    String fileExt = "fdsym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    List<File> files = FileFinder.getFiles(mp, qualifiedModelName, fileExt);
    assertEquals(0, files.size());
  }

  @Test
  public void findFilesThrowsError() {
    String fileExt = "*sym";
    String qualifiedModelName = "de.monticore.io.Model1";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA1294"));
  }

  @Test
  public void findFilesNoLoadedFile() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(1,list.size());
    assertEquals("de\\monticore\\io\\Model2.mc4", list.get(0).getQualifiedPath().toString());
  }

  @Test
  public void findFilesOneLoadedFile() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    loaded.add("de\\monticore\\io\\Model2.mc4");
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(0,list.size());
  }

  @Test
  public void findFilesNoFinding() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model22";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(0,list.size());
  }


  @Test
  public void findFilesNoLoadedFileII() {
    String fileExt = "*4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    List<ModelCoordinate> list = FileFinder.findFiles(mp,qualifiedModelName,fileExt,loaded );
    assertEquals(2,list.size());
    assertEquals("de\\monticore\\io\\Model2.cd4", list.get(0).getQualifiedPath().toString());
    assertEquals("de\\monticore\\io\\Model2.mc4", list.get(1).getQualifiedPath().toString());
  }

  @Test
  public void findFileThrowsError() {
    String fileExt = "*4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA7654"));
  }

  @Test
  public void findFileOneFinding() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertTrue(opt.isPresent());
    assertEquals("de\\monticore\\io\\Model2.mc4", opt.get().getQualifiedPath().toString());
  }

  @Test
  public void findFileNoFinding() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model2";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    loaded.add("de\\monticore\\io\\Model2.mc4");
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertFalse(opt.isPresent());
  }

  @Test
  public void findFileNoFindingII() {
    String fileExt = "mc4";
    String qualifiedModelName = "de.monticore.io.Model22";
    List<Path> entries = new ArrayList<>();
    entries.add(Paths.get("src\\test\\models"));
    entries.add(Paths.get("src\\test\\resources"));
    ModelPath mp = new ModelPath(entries);
    Set<String> loaded = new HashSet<>();
    Optional <ModelCoordinate> opt = FileFinder.findFile(mp,qualifiedModelName,fileExt,loaded );
    assertFalse(opt.isPresent());
  }
}

