/* (c) https://github.com/MontiCore/monticore */

import a.AMill;
import a.AMillState;
import b.BMill;
import b.BMillState;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MillCompositionStateTest extends AbstractMCTest {
  @Test
  public void testA() throws IOException {
    Path aPath = Paths.get("AExample"), bPath = Paths.get("BExample");
    AMill.init();
    AMill.globalScope().getSymbolPath().addEntry(aPath);

    // Copy the "state" as a reference.
    // Later changes to the Mill are still reflected in this variable
    AMillState aState = AMill.asNonStatic();

    Assertions.assertEquals(aState.mill, AMill.getMill());
    // Test, that the path contains the one element
    Assertions.assertTrue(AMill.globalScope().getSymbolPath().getEntries().stream().anyMatch(p -> p.toString().endsWith("AExample")));
    Assertions.assertEquals(1, AMill.globalScope().getSymbolPath().getEntries().size());

    // Switch to Mill B (without cleaning any scopes)
    BMill.init(); // B extends A
    BMillState bState = BMill.asNonStatic();

    Assertions.assertNotEquals(aState.mill, AMill.getMill());
    Assertions.assertEquals(bState.mill, BMill.getMill());
    Assertions.assertEquals(bState.aMill, AMill.getMill());
    Assertions.assertEquals(0, AMill.globalScope().getSymbolPath().getEntries().size());
    Assertions.assertEquals(0, BMill.globalScope().getSymbolPath().getEntries().size());

    AMill.globalScope().getSymbolPath().addEntry(bPath);
    Assertions.assertTrue(AMill.globalScope().getSymbolPath().getEntries().stream().anyMatch(p -> p.toString().endsWith("BExample")));
    Assertions.assertTrue(BMill.globalScope().getSymbolPath().getEntries().stream().anyMatch(p -> p.toString().endsWith("BExample")));
    Assertions.assertEquals(1, AMill.globalScope().getSymbolPath().getEntries().size());
    Assertions.assertEquals(1, BMill.globalScope().getSymbolPath().getEntries().size());


    // Switch back to A's state
    AMill.load(aState);

    Assertions.assertEquals(aState.mill, AMill.getMill());
    Assertions.assertNotEquals(bState.mill, AMill.getMill());
    Assertions.assertEquals(bState.mill, BMill.getMill()); // Yes, this is leaking

    // Check that the old global scope is also relevant
    Assertions.assertTrue(AMill.globalScope().getSymbolPath().getEntries().stream().anyMatch(p -> p.toString().endsWith("AExample")));
    Assertions.assertEquals(1, AMill.globalScope().getSymbolPath().getEntries().size());

  }

}
