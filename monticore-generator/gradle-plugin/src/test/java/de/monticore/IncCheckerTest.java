/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.hash.Hashing;
import com.google.common.io.CharSource;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the {@link IncChecker} testing against the expected incCheck result after changing the files.
 */
@RunWith(Parameterized.class)
public class IncCheckerTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  // Test for the default mc4, a short sc, and a longer file ending
  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> fileEndings() {
    return Arrays.asList(new Object[][]{
      {"mc4"}, {"sc"}, {"longerEnding"}
    });
  }
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  protected final String fileEnding;

  public IncCheckerTest(String fileEnding) {
    this.fileEnding = fileEnding;
  }

  @Test
  public void testIncCheck() throws IOException {
    File tempDir = temporaryFolder.newFolder();
    File outDir = new File(tempDir, "out");
    File repDir = new File(tempDir, "reports");
    outDir.mkdirs();
    repDir.mkdirs();
    String modelName = "IncCheckExample";
    Logger logger = LoggerFactory.getLogger("nop");

    // The input model
    File inputFile = new File(tempDir, "in." + fileEnding);
    Files.writeToFile(CharSource.wrap("file content").asByteSource(StandardCharsets.UTF_8).openStream(), inputFile);

    // An existing file which will later be checked for
    File existingHWFile = new File(tempDir, "HW1.fileExt");
    Files.writeToFile(CharSource.wrap("hand written content").asByteSource(StandardCharsets.UTF_8).openStream(), existingHWFile);

    // A non-existing file which will later be checked for
    File missingHWFile = new File(tempDir, "HW2.fileExt");

    MCPath mcPath = new MCPath(tempDir.toPath());

    // Test that the MCPath is behaving correctly for both HW Files
    Assert.assertTrue("Existing file not found in MCPath", mcPath.find(existingHWFile.getName()).isPresent());
    Assert.assertFalse("Missing file found in MCPath", mcPath.find(missingHWFile.getName()).isPresent());

    // Create the IncGenGradleCheck file and fill its content
    File modelRepDir = new File(repDir, modelName.replaceAll("\\.", "/").toLowerCase());
    File incGenGradleCheckFile = new File(repDir + File.separator
      + modelName.replaceAll("\\.", "/").toLowerCase() + File.separator + "IncGenGradleCheck.txt");
    modelRepDir.mkdirs();
    incGenGradleCheckFile.createNewFile();
    Files.writeToFile(CharSource.wrap(
      calcChacheEntry(inputFile) + "\n" +
      calcHwcEntry(existingHWFile) + "\n" +
      calcGenEntry(missingHWFile) + "\n"
    ).asByteSource(StandardCharsets.UTF_8).openStream(), incGenGradleCheckFile);

    // Has the IncGenGradleCheck file been created?
    Assert.assertTrue("IncGenGradleCheck.txt does not exists: " + incGenGradleCheckFile.getAbsolutePath(), incGenGradleCheckFile.exists());

    // Next, actually test the IncCheck
    // First without any changes
    Assert.assertTrue("IncCheck without changes failed", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""));

    // Check when a HW file has been added
    missingHWFile.createNewFile();
    Assert.assertFalse("IncCheck with added HW file did not fire", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""));
    missingHWFile.delete();

    // Test with no changes again (after deleting the added HW file)
    Assert.assertTrue("IncCheck without changes (after deleting) failed", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""));

    // Delete the existing HW file and test
    existingHWFile.delete();
    Assert.assertFalse("IncCheck with deleted HW file did not fire", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""));
    existingHWFile.createNewFile();

    // Test with no changes again (after re-adding the deleted HW file)
    Assert.assertTrue("IncCheck without changes (after re-adding) failed", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""));

    // Change input model/content
    Files.writeToFile(CharSource.wrap("new file content").asByteSource(StandardCharsets.UTF_8).openStream(), inputFile);
    Assert.assertFalse("IncCheck with changed input model did not fire", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""));
    
    assertTrue(Log.getFindings().isEmpty());
  }

  private String calcChacheEntry(File file) throws IOException {
    StringBuilder cacheEntry = new StringBuilder();
    cacheEntry.append(fileEnding + ":");
    cacheEntry.append(file.getAbsolutePath());
    cacheEntry.append(" ");
    cacheEntry.append(com.google.common.io.Files.asByteSource(file).hash(Hashing.md5()).toString());
    return cacheEntry.toString();
  }

  private String calcHwcEntry(File file) {
    StringBuilder hwcEntry = new StringBuilder();
    hwcEntry.append("hwc:");
    hwcEntry.append(file.getAbsolutePath());
    return hwcEntry.toString();
  }

  private String calcGenEntry(File file) {
    StringBuilder genEntry = new StringBuilder();
    genEntry.append("gen:");
    genEntry.append(file.getAbsolutePath());
    return genEntry.toString();
  }
}