/* (c) https://github.com/MontiCore/monticore */

import bluea.BlueAMill;
import bluea._symboltable.PlaceSymbol;
import blueb.BlueBMill;
import blueb._symboltable.BluePlaceSymbol;
import blueb._symboltable.RedPlaceSymbol;
import bluec.BlueCMill;
import bluec._ast.ASTLightBluePlace;
import bluec._symboltable.BlueCSymbols2Json;
import bluec._symboltable.IBlueCArtifactScope;
import bluec._symboltable.LightBluePlaceSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * This test ensures that unknown symbol kinds are implicitly adapted to their super-kinds.
 * Thus, when resolving for a symbol of a kind, the kinds of (unknown) sublanguages are also present & loaded
 */
public class ImplicitSymbolHierarchyTest {


  @TempDir
  protected Path tempDir;

  protected Path symbolTableFile;

  @BeforeEach
  public void before() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);

    symbolTableFile = tempDir.resolve("LB1.bluec.sym");

    BlueCMill.init();

    Optional<ASTLightBluePlace> astOpt = BlueCMill.parser()
            .parse_String("lightBluePlace LB1 { bluePlace B1 {} redPlace R1 {} }");
    Assertions.assertTrue(astOpt.isPresent());

    IBlueCArtifactScope st = BlueCMill.scopesGenitorDelegator().createFromAST(astOpt.get());

    BlueCSymbols2Json blueCSymbols2Json = new BlueCSymbols2Json();
    blueCSymbols2Json.store(st, symbolTableFile.toAbsolutePath().toString());

    BlueCMill.reset();
  }

  @Test
  public void testBlueC() throws IOException {
    BlueCMill.init();
    // We have to add the parent (not the file itself)
    BlueCMill.globalScope().getSymbolPath().addEntry(tempDir);

    Assertions.assertEquals(1, BlueCMill.globalScope().resolveLightBluePlaceMany("LB1").size());
    Assertions.assertEquals(1, BlueCMill.globalScope().resolveBluePlaceMany("LB1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolveBluePlaceMany("LB1").size()); // lang comp
    Assertions.assertEquals(1, BlueCMill.globalScope().resolvePlaceMany("LB1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolvePlaceMany("LB1").size()); // lang comp
    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1").size()); // lang comp

    Assertions.assertEquals(0, BlueCMill.globalScope().resolveLightBluePlaceMany("LB1.B1").size());
    Assertions.assertEquals(1, BlueCMill.globalScope().resolveBluePlaceMany("LB1.B1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolveBluePlaceMany("LB1.B1").size()); // lang comp
    Assertions.assertEquals(1, BlueCMill.globalScope().resolvePlaceMany("LB1.B1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolvePlaceMany("LB1.B1").size()); // lang comp
    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1.B1").size()); // lang comp

    Assertions.assertEquals(0, BlueCMill.globalScope().resolveLightBluePlaceMany("LB1.R1").size());
    Assertions.assertEquals(1, BlueCMill.globalScope().resolveRedPlaceMany("LB1.R1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolveRedPlaceMany("LB1.R1").size()); // lang comp
    Assertions.assertEquals(1, BlueCMill.globalScope().resolvePlaceMany("LB1.R1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolvePlaceMany("LB1.R1").size()); // lang comp
    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1.R1").size()); // lang comp

    BlueCMill.reset();
  }

  @Test
  public void testBlueB() throws IOException {
    BlueBMill.init();
    // We have to add the parent (not the file itself)
    BlueBMill.globalScope().getSymbolPath().addEntry(tempDir);

    Assertions.assertEquals(1, BlueBMill.globalScope().resolveBluePlaceMany("LB1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolvePlaceMany("LB1").size());
    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1").size());

    Assertions.assertEquals(1, BlueBMill.globalScope().resolveBluePlaceMany("LB1.B1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolvePlaceMany("LB1.B1").size());
    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1.B1").size()); // lang comp

    Assertions.assertEquals(1, BlueBMill.globalScope().resolveRedPlaceMany("LB1.R1").size());
    Assertions.assertEquals(1, BlueBMill.globalScope().resolvePlaceMany("LB1.R1").size());
    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1.R1").size()); // lang comp

    BlueBMill.reset();
  }

  @Test
  public void testBlueA() throws IOException {
    BlueAMill.init();
    // We have to add the parent (not the file itself)
    BlueAMill.globalScope().getSymbolPath().addEntry(tempDir);

    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1").size());

    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1.B1").size());

    Assertions.assertEquals(1, BlueAMill.globalScope().resolvePlaceMany("LB1.R1").size());

    BlueAMill.reset();
  }


  @Test
  public void testJson() throws IOException {
    JsonElement jsonST = JsonParser.parse(Files.readString(symbolTableFile));
    Assertions.assertTrue(jsonST.isJsonObject());
    Assertions.assertTrue(jsonST.getAsJsonObject().hasObjectMember("symbolHierarchy"));
    JsonObject symbolHierarchy = jsonST.getAsJsonObject().getObjectMember("symbolHierarchy");

    // C.LightBluePlaceSymbol -> B.BlueSymbol
    Assertions.assertTrue(symbolHierarchy.hasStringMember(LightBluePlaceSymbol.class.getName()));
    Assertions.assertEquals(BluePlaceSymbol.class.getName(),
                            symbolHierarchy.getStringMember(LightBluePlaceSymbol.class.getName()));

    // B.BluePlaceSymbol -> A.PlaceSymbol
    Assertions.assertTrue(symbolHierarchy.hasStringMember(BluePlaceSymbol.class.getName()));
    Assertions.assertEquals(PlaceSymbol.class.getName(),
                            symbolHierarchy.getStringMember(BluePlaceSymbol.class.getName()));

    // B.RedPlaceSymbol -> A.PlaceSymbol
    Assertions.assertTrue(symbolHierarchy.hasStringMember(RedPlaceSymbol.class.getName()));
    Assertions.assertEquals(PlaceSymbol.class.getName(),
                            symbolHierarchy.getStringMember(RedPlaceSymbol.class.getName()));

    Assertions.assertEquals(3, symbolHierarchy.sizeMembers());
  }

  @AfterEach
  public void after() {
    Log.getFindings().clear();
  }
}