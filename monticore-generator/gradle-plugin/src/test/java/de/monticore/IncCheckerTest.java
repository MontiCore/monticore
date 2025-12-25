/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.hash.Hashing;
import com.google.common.io.CharSource;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Files;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * This class tests the {@link IncChecker} testing against the expected incCheck result after changing the files.
 */
public class IncCheckerTest {

  @TempDir
  public File temporaryFolder;
  
  
  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @ParameterizedTest
  @ValueSource(strings = {"mc4", "sc", "longerEnding"})
  public void testIncCheck(String fileEnding) throws IOException {
    File tempDir = new File(temporaryFolder, "tempDir");
    tempDir.createNewFile();
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
    assertTrue(mcPath.find(existingHWFile.getName()).isPresent(), "Existing file not found in MCPath");
    assertFalse(mcPath.find(missingHWFile.getName()).isPresent(), "Missing file found in MCPath");

    // Create the IncGenGradleCheck file and fill its content
    File modelRepDir = new File(repDir, modelName.replaceAll("\\.", "/").toLowerCase());
    File incGenGradleCheckFile = new File(repDir + File.separator
      + modelName.replaceAll("\\.", "/").toLowerCase() + File.separator + "IncGenGradleCheck.txt");
    modelRepDir.mkdirs();
    incGenGradleCheckFile.createNewFile();
    Files.writeToFile(CharSource.wrap(
      calcChacheEntry(inputFile, fileEnding) + "\n" +
      calcHwcEntry(existingHWFile) + "\n" +
      calcGenEntry(missingHWFile) + "\n"
    ).asByteSource(StandardCharsets.UTF_8).openStream(), incGenGradleCheckFile);

    // Has the IncGenGradleCheck file been created?
    assertTrue(incGenGradleCheckFile.exists(),
        "IncGenGradleCheck.txt does not exists: " + incGenGradleCheckFile.getAbsolutePath());

    // Next, actually test the IncCheck
    // First without any changes
    assertTrue(IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""),
        "IncCheck without changes failed");

    // Check when a HW file has been added
    missingHWFile.createNewFile();
    assertFalse(IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""),
        "IncCheck with added HW file did not fire");
    missingHWFile.delete();

    // Test with no changes again (after deleting the added HW file)
    assertTrue(IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""),
        "IncCheck without changes (after deleting) failed");

    // Delete the existing HW file and test
    existingHWFile.delete();
    assertFalse(IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""),
        "IncCheck with deleted HW file did not fire");
    existingHWFile.createNewFile();

    // Test with no changes again (after re-adding the deleted HW file)
    assertTrue(IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""),
        "IncCheck without changes (after re-adding) failed");

    // Change input model/content
    Files.writeToFile(CharSource.wrap("new file content").asByteSource(StandardCharsets.UTF_8).openStream(), inputFile);
    assertFalse(IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding, ""),
        "IncCheck with changed input model did not fire");
    
    assertTrue(Log.getFindings().isEmpty());
  }

  private String calcChacheEntry(File file, String fileEnding) throws IOException {
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