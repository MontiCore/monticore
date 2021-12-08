package de.monticore.reporting;

import com.google.common.io.CharSource;
import de.monticore.ast.ASTCNodeMock;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import de.monticore.io.paths.MCPath;
import de.monticore.utils.IncChecker;
import de.se_rwth.commons.Files;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
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

/**
 * This class tests both the {@link IncGenGradleReporter} and {@link IncChecker}
 * by manually reporting on an input model and hand-written file checks.
 * Finally, it tests against the expected incCheck result after changing the files.
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

    // Init reporting with an IncGenGradleReporter
    Reporting.init(outDir.toString(), repDir.toString(), modelName1 -> {
      ReportManager reports = new ReportManager(repDir.toString());
      reports.addReportEventHandler(new IncGenGradleReporter(repDir.toString(), modelName1, fileEnding));
      return reports;
    });
    Reporting.on(modelName);
    // Report on the input file
    Reporting.reportModelStart(ASTCNodeMock.INSTANCE, modelName, "");
    Reporting.reportParseInputFile(inputFile.toPath(), modelName);

    // Report on both HW files
    Reporting.reportHWCExistenceCheck(mcPath, existingHWFile.toPath(), mcPath.find(existingHWFile.getName()));
    Reporting.reportHWCExistenceCheck(mcPath, missingHWFile.toPath(), mcPath.find(missingHWFile.getName()));

    Reporting.reportModelEnd(modelName, "");
    Reporting.flush(ASTCNodeMock.INSTANCE);


    // Has the IncGenGradleCheck file been created?
    File incGenGradleCheckFile = new File(repDir + File.separator
            + modelName.replaceAll("\\.", "/").toLowerCase() + File.separator + "IncGenGradleCheck.txt");
    Assert.assertTrue("IncGenGradleCheck.txt does not exists: " + incGenGradleCheckFile.getAbsolutePath(), incGenGradleCheckFile.exists());

    // Next, actually test the IncCheck
    // First without any changes
    Assert.assertTrue("IncCheck without changes failed", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding));

    // Check when a HW file has been added
    missingHWFile.createNewFile();
    Assert.assertFalse("IncCheck with added HW file did not fire", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding));
    missingHWFile.delete();

    // Test with no changes again (after deleting the added HW file)
    Assert.assertTrue("IncCheck without changes (after deleting) failed", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding));

    // Delete the existing HW file and test
    existingHWFile.delete();
    Assert.assertFalse("IncCheck with deleted HW file did not fire", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding));
    existingHWFile.createNewFile();

    // Test with no changes again (after re-adding the deleted HW file)
    Assert.assertTrue("IncCheck without changes (after re-adding) failed", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding));

    // Change input model/content
    Files.writeToFile(CharSource.wrap("new file content").asByteSource(StandardCharsets.UTF_8).openStream(), inputFile);
    Assert.assertFalse("IncCheck with changed input model did not fire", IncChecker.incCheck(incGenGradleCheckFile, modelName, logger, fileEnding));
  }

}
