/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.Iterators;

/**
 * Unit tests for IterablePath.
 *
 */
public class IterablePathTest {
  
  static final File PATH = new File("src/test/resources/paths/");
  
  static final File PATH_1 = new File("src/test/resources/paths/1");
  
  static final File PATH_2 = new File("src/test/resources/paths/2/");
  
  static final File[] BINARY_PATH = { PATH_1, PATH_2 };
  
  static final String FTL_EXT = "ftl";
  
  static final String TXT_EXT = "txt";
  
  /* All possible qualified paths to the test files */
  static final Path AFILE_TXT = Paths.get("AFile.txt");
  
  static final Path AFILE_FTL = Paths.get("AFile.ftl");
  
  static final Path A_AFILE_TXT = Paths.get("a/AFile.txt");
  
  static final Path A_AFILE_FTL = Paths.get("a/AFile.ftl");
  
  static final Path A1_AFILE_TXT = Paths.get("1/a/AFile.txt");
  
  static final Path A1_AFILE_FTL = Paths.get("1/a/AFile.ftl");
  
  static final Path A2_AFILE_TXT = Paths.get("2/a/AFile.txt");
  
  static final Path A2_AFILE_FTL = Paths.get("2/a/AFile.ftl");
  
  /* An explicit path argument */
  static final File EXPLICIT_PATH = new File("src/test/resources/paths/2/a/AFile.ftl");
  
  static final File[] MIXED_PATH = { PATH_1, PATH_2, EXPLICIT_PATH };
  
  @Test
  public void simpleBinaryPath() {
    IterablePath testee = IterablePath.from(Arrays.asList(BINARY_PATH), TXT_EXT);
    
    Iterator<Path> sizeTest = testee.get();
    assertEquals(2, Iterators.size(sizeTest));
    // the second a/AFile.txt is not contained; it is hidden (first one wins)
    
    assertTrue(testee.exists(AFILE_TXT));
    assertTrue(testee.exists(A_AFILE_TXT));
    // this qualified entry does not exist as the "1" is not regarded as part of
    // the qualified path; it is part of the path entry itself (the parent
    // directory if you will)
    assertFalse(testee.exists(A1_AFILE_TXT));
  }
  
  @Test
  public void testSuperPath() {
    IterablePath testee = IterablePath.from(PATH, FTL_EXT);
    
    Iterator<Path> sizeTest = testee.get();
    assertEquals(3, Iterators.size(sizeTest));
  }
  
  @Test
  public void testMixingDirsAndFiles() {
    IterablePath testee = IterablePath.from(Arrays.asList(BINARY_PATH), FTL_EXT);
    
    // test that the wrong resolved path (2/a) is not contained
    Path a1_afile_ftl = new File("src/test/resources/paths/1/a/AFile.ftl").toPath();
    Path a2_afile_ftl = new File("src/test/resources/paths/2/a/AFile.ftl").toPath();
    
    assertTrue(testee.exists(A_AFILE_FTL));
    assertNotEquals(a2_afile_ftl, testee.getResolvedPath(A_AFILE_FTL).get());
    assertEquals(a1_afile_ftl, testee.getResolvedPath(A_AFILE_FTL).get());
    
    Iterator<Path> sizeTest = testee.get();
    assertEquals(2, Iterators.size(sizeTest));
    
    // now let's add it explicitly as a file
    testee = IterablePath.from(Arrays.asList(MIXED_PATH), FTL_EXT);
    
    assertTrue(testee.exists(A_AFILE_FTL));
    assertNotEquals(a2_afile_ftl, testee.getResolvedPath(A_AFILE_FTL).get());
    assertEquals(a1_afile_ftl, testee.getResolvedPath(A_AFILE_FTL).get());
    
    sizeTest = testee.get();
    assertEquals(3, Iterators.size(sizeTest));
    
    // however the previously not contained path is now an explicit entry
    assertTrue(testee.exists(a2_afile_ftl));
    // this entry was previously hidden (since another path element already
    // contained the same qualified path 1/a/AFile.ftl hid 2/a/AFile.ftl; but
    // after adding the latter as an explicit file entry it is accessible
    // (though not in a qualified manner, i.e., 2/a/AFile.ftl)
  }
  
}
