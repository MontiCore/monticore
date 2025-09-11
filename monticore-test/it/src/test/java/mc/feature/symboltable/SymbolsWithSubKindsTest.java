/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill;
import mc.feature.symboltable.symbolswithsubkinds._ast.*;
import mc.feature.symboltable.symbolswithsubkinds._parser.SymbolsWithSubKindsParser;
import mc.feature.symboltable.symbolswithsubkinds._symboltable.ISymbolsWithSubKindsGlobalScope;
import mc.feature.symboltable.symbolswithsubkinds._symboltable.ISymbolsWithSubKindsScope;
import mc.feature.symboltable.symbolswithsubkinds._symboltable.SymbolsWithSubKindsScope;
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
        SymbolsWithSubKindsMill.reset();
        SymbolsWithSubKindsMill.init();
    }

    @Test
    public void testIScopeSpanningSymbol() throws IOException {
        // parse model
        SymbolsWithSubKindsParser scopeAttributesParser = new SymbolsWithSubKindsParser();
        Optional<ASTZoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st");
        Assertions.assertFalse(scopeAttributesParser.hasErrors());
        Assertions.assertTrue(astSup.isPresent());

        // create symboltable
        ISymbolsWithSubKindsGlobalScope globalScope = SymbolsWithSubKindsMill.globalScope();
        globalScope.setFileExt("st");
        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

        ISymbolsWithSubKindsScope scope = SymbolsWithSubKindsMill
                .scopesGenitorDelegator().createFromAST(astSup.get());

        Assertions.assertSame(1, scope.getSubScopes().size());
        ISymbolsWithSubKindsScope spannedScope =  scope.getSubScopes().get(0);
        Assertions.assertInstanceOf(SymbolsWithSubKindsScope.class , spannedScope);

        // check if the methods return the correct number of symbols
        // see monticore-test/it/src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st
        Assertions.assertSame(1, spannedScope.getFordSymbolsWithSubKinds().size());
        Assertions.assertSame(1, spannedScope.getVolvoSymbolsWithSubKinds().size());
        Assertions.assertSame(3,spannedScope.getTruckSymbolsWithSubKinds().size());
        Assertions.assertSame(1,spannedScope.getBeatleSymbolsWithSubKinds().size());
        Assertions.assertSame(1, spannedScope.getMazdaSymbolsWithSubKinds().size());
        Assertions.assertSame(3,spannedScope.getCabrioletSymbolsWithSubKinds().size());
        Assertions.assertSame(7,spannedScope.getCarSymbolsWithSubKinds().size());
        Assertions.assertSame(1, spannedScope.getDogSymbolsWithSubKinds().size());
        Assertions.assertSame(1, spannedScope.getCatSymbolsWithSubKinds().size());
        Assertions.assertSame(1, spannedScope.getFishSymbolsWithSubKinds().size());
        Assertions.assertSame(4,spannedScope.getAnimalSymbolsWithSubKinds().size());
        Assertions.assertSame(11, spannedScope.getZooSymbolsWithSubKinds().size());

        Assertions.assertEquals("carFordName", spannedScope.getFordSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("carVolvoName", spannedScope.getVolvoSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("carTruckName", spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(ASTTruck.class)).findAny().get().getName());
        Assertions.assertEquals("carMazdaName", spannedScope.getMazdaSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("carBeatleName", spannedScope.getBeatleSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(ASTCabriolet.class)).findAny().get().getName());
        Assertions.assertEquals("carCarName", spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(ASTCar.class)).findAny().get().getName());
        Assertions.assertEquals("animalDogName", spannedScope.getDogSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("animalCatName", spannedScope.getCatSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("animalFishName", spannedScope.getFishSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("animalAnimalName", spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(ASTAnimal.class)).findAny().get().getName());
    }

    /**
     * This test ensures that all expected classes are generated. Otherwise, the test will not compile
     */
    @SuppressWarnings("unused")
    @Test
    public void test() {
        ASTZoo zoo = new ASTZooBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTSymbolsWithSubKindsNode.class, zoo);
        ASTCar car = new ASTCarBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTVehicle.class, car);
        Assertions.assertInstanceOf(ASTZoo.class, car);
        ASTCabriolet cabriolet = new ASTCabrioletBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTCar.class, cabriolet);
        ASTBeatle beatle = new ASTBeatleBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTCabriolet.class, beatle);
        ASTMazda mazda = new ASTMazdaBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTCabriolet.class, mazda);
        ASTTruck truck = new ASTTruckBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTCar.class, truck);
        ASTFord ford = new ASTFordBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTTruck.class, ford);
        ASTVolvo volvo = new ASTVolvoBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTTruck.class, volvo);
        ASTAnimal animal = new ASTAnimalBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTLeg.class, animal);
        Assertions.assertInstanceOf(ASTZoo.class, animal);
        ASTDog dog = new ASTDogBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTAnimal.class, dog);
        ASTCat cat = new ASTCatBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTAnimal.class, cat);
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }
}
