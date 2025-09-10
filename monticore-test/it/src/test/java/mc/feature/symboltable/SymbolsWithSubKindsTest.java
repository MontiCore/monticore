/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.notopscope.NoTopScopeMill;
import mc.feature.symboltable.notopscope._ast.ASTFoo;
import mc.feature.symboltable.notopscope._parser.NoTopScopeParser;
import mc.feature.symboltable.notopscope._symboltable.INoTopScopeArtifactScope;
import mc.feature.symboltable.notopscope._symboltable.INoTopScopeGlobalScope;
import mc.feature.symboltable.symbolswithsubkinds._ast.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;


public class SymbolsWithSubKindsTest extends GeneratorIntegrationsTest {

    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
    }

    @Test
    public void testIScopeSpanningSymbol() throws IOException {
        // parse model
        NoTopScopeParser scopeAttributesParser = new NoTopScopeParser();
        Optional<ASTFoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st.st");
        Assertions.assertFalse(scopeAttributesParser.hasErrors());
        Assertions.assertTrue(astSup.isPresent());

        // create symboltable
        INoTopScopeGlobalScope globalScope = NoTopScopeMill.globalScope();
        globalScope.setFileExt("st");
        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

        INoTopScopeArtifactScope scope = NoTopScopeMill
                .scopesGenitorDelegator().createFromAST(astSup.get());



    }

    /**
     * This test ensures that all expected classes are generated. Otherwise, the test will not compile
     */
    @SuppressWarnings("unused")
    @Test
    public void test() {
        ASTZoo zoo = new ASTZooBuilder().build();
        Assertions.assertTrue(zoo instanceof ASTSymbolsWithSubKindsNode);
        ASTCar car = new ASTCarBuilder().build();
        Assertions.assertTrue(car instanceof ASTVehicle);
        ASTCabriolet cabriolet = new ASTCabrioletBuilder().build();
        Assertions.assertTrue(cabriolet instanceof ASTCar);
        ASTBeatle beatle = new ASTBeatleBuilder().build();
        Assertions.assertTrue(beatle instanceof ASTCabriolet);
        ASTMazda mazda = new ASTMazdaBuilder().build();
        Assertions.assertTrue(mazda instanceof ASTCabriolet);
        ASTTruck truck = new ASTTruckBuilder().build();
        Assertions.assertTrue(truck instanceof ASTCar);
        ASTFord ford = new ASTFordBuilder().build();
        Assertions.assertTrue(ford instanceof ASTTruck);
        ASTVolvo volvo = new ASTVolvoBuilder().build();
        Assertions.assertTrue(volvo instanceof ASTTruck);
        ASTAnimal animal = new ASTAnimalBuilder().build();
        Assertions.assertTrue(animal instanceof ASTLeg);
        ASTDog dog = new ASTDogBuilder().build();
        Assertions.assertTrue(dog instanceof ASTAnimal);
        ASTCat cat = new ASTCatBuilder().build();
        Assertions.assertTrue(cat instanceof ASTAnimal);
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }
}
