/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class SymbolsWithSubKindsTest extends GeneratorIntegrationsTest {

    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
        mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill.reset();
        mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill.init();
        mc.feature.symboltable.symbolswithsubkindswithmultipleparents.SymbolsWithSubKindsWithMultipleParentsMill.reset();
        mc.feature.symboltable.symbolswithsubkindswithmultipleparents.SymbolsWithSubKindsWithMultipleParentsMill.init();
    }

    @Test
    public void testSymbol() throws IOException {
        // parse model
        mc.feature.symboltable.symbolswithsubkinds._parser.SymbolsWithSubKindsParser scopeAttributesParser = new mc.feature.symboltable.symbolswithsubkinds._parser.SymbolsWithSubKindsParser();
        Optional<mc.feature.symboltable.symbolswithsubkinds._ast.ASTZoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st");
        Assertions.assertFalse(scopeAttributesParser.hasErrors());
        Assertions.assertTrue(astSup.isPresent());

        // create symboltable
        mc.feature.symboltable.symbolswithsubkinds._symboltable.ISymbolsWithSubKindsGlobalScope globalScope = mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill.globalScope();
        globalScope.setFileExt("st");
        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

        mc.feature.symboltable.symbolswithsubkinds._symboltable.ISymbolsWithSubKindsScope scope = mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill
                .scopesGenitorDelegator().createFromAST(astSup.get());

        Assertions.assertSame(1, scope.getSubScopes().size());
        mc.feature.symboltable.symbolswithsubkinds._symboltable.ISymbolsWithSubKindsScope spannedScope =  scope.getSubScopes().get(0);
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._symboltable.SymbolsWithSubKindsScope.class , spannedScope);

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

        Assertions.assertEquals("carFordName",      spannedScope.getFordSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getVolvoSymbolsWithSubKinds().values().get(0).getName());

        Assertions.assertEquals("carFordName",      spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTFord.class)).findAny().get().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTVolvo.class)).findAny().get().getName());
        Assertions.assertEquals("carTruckName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruck.class)).findAny().get().getName());

        Assertions.assertEquals("carMazdaName",     spannedScope.getMazdaSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getBeatleSymbolsWithSubKinds().values().get(0).getName());

        Assertions.assertEquals("carMazdaName",     spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTMazda.class)).findAny().get().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTBeatle.class)).findAny().get().getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabriolet.class)).findAny().get().getName());

        Assertions.assertEquals("carFordName",      spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTFord.class)).findAny().get().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTVolvo.class)).findAny().get().getName());
        Assertions.assertEquals("carTruckName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruck.class)).findAny().get().getName());
        Assertions.assertEquals("carMazdaName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTMazda.class)).findAny().get().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTBeatle.class)).findAny().get().getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabriolet.class)).findAny().get().getName());
        Assertions.assertEquals("carCarName",       spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCar.class)).findAny().get().getName());

        Assertions.assertEquals("animalDogName",    spannedScope.getDogSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("animalCatName",    spannedScope.getCatSymbolsWithSubKinds().values().get(0).getName());
        Assertions.assertEquals("animalFishName",   spannedScope.getFishSymbolsWithSubKinds().values().get(0).getName());

        Assertions.assertEquals("animalDogName",    spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTDog.class)).findAny().get().getName());
        Assertions.assertEquals("animalCatName",    spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCat.class)).findAny().get().getName());
        Assertions.assertEquals("animalFishName",   spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTFish.class)).findAny().get().getName());
        Assertions.assertEquals("animalAnimalName", spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTAnimal.class)).findAny().get().getName());

        Assertions.assertEquals("carFordName",      spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTFord.class)).findAny().get().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTVolvo.class)).findAny().get().getName());
        Assertions.assertEquals("carTruckName",     spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruck.class)).findAny().get().getName());
        Assertions.assertEquals("carMazdaName",     spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTMazda.class)).findAny().get().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTBeatle.class)).findAny().get().getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabriolet.class)).findAny().get().getName());
        Assertions.assertEquals("carCarName",       spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCar.class)).findAny().get().getName());
        Assertions.assertEquals("animalDogName",    spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTDog.class)).findAny().get().getName());
        Assertions.assertEquals("animalCatName",    spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCat.class)).findAny().get().getName());
        Assertions.assertEquals("animalFishName",   spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTFish.class)).findAny().get().getName());
        Assertions.assertEquals("animalAnimalName", spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkinds._ast.ASTAnimal.class)).findAny().get().getName());
    }

    @Test
    //TODO once diamond type extention of symbols via interface symbols is fixed, this test can be enabled again    eventually replacing the test above
    public void testSymbolsWithMultipleParents() throws IOException {
//        // parse model
//        mc.feature.symboltable.symbolswithsubkindswithmultipleparents._parser.SymbolsWithSubKindsWithMultipleParentsParser scopeAttributesParser = new mc.feature.symboltable.symbolswithsubkindswithmultipleparents._parser.SymbolsWithSubKindsWithMultipleParentsParser();
//        Optional<mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTZoo> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SymbolsWithSubKindsWithMultipleParents.st");
//        Assertions.assertFalse(scopeAttributesParser.hasErrors());
//        Assertions.assertTrue(astSup.isPresent());
//
//        // create symboltable
//        mc.feature.symboltable.symbolswithsubkindswithmultipleparents._symboltable.ISymbolsWithSubKindsWithMultipleParentsGlobalScope globalScope =
//                mc.feature.symboltable.symbolswithsubkindswithmultipleparents.SymbolsWithSubKindsWithMultipleParentsMill.globalScope();
//        globalScope.setFileExt("st");
//        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));
//
//        mc.feature.symboltable.symbolswithsubkindswithmultipleparents._symboltable.ISymbolsWithSubKindsWithMultipleParentsScope scope =
//                mc.feature.symboltable.symbolswithsubkindswithmultipleparents.SymbolsWithSubKindsWithMultipleParentsMill
//                .scopesGenitorDelegator().createFromAST(astSup.get());
//
//        Assertions.assertSame(1, scope.getSubScopes().size());
//        mc.feature.symboltable.symbolswithsubkindswithmultipleparents._symboltable.ISymbolsWithSubKindsWithMultipleParentsScope spannedScope =  scope.getSubScopes().get(0);
//        Assertions.assertInstanceOf( mc.feature.symboltable.symbolswithsubkindswithmultipleparents._symboltable.ISymbolsWithSubKindsWithMultipleParentsScope.class , spannedScope);
//
//
//        // check if the methods return the correct number of symbols
//        // see monticore-test/it/src/test/resources/mc/feature/symboltable/SymbolsWithSubKindsWithMultipleParents.st
//        Assertions.assertSame(1, spannedScope.getFordSymbolsWithSubKinds().size());
//        Assertions.assertSame(1, spannedScope.getVolvoSymbolsWithSubKinds().size());
//        Assertions.assertSame(3,spannedScope.getTruckSymbolsWithSubKinds().size());
//        Assertions.assertSame(1,spannedScope.getBeatleSymbolsWithSubKinds().size());
//        Assertions.assertSame(1, spannedScope.getMazdaSymbolsWithSubKinds().size());
//        Assertions.assertSame(3,spannedScope.getCabrioletSymbolsWithSubKinds().size());
//        Assertions.assertSame(7,spannedScope.getCarSymbolsWithSubKinds().size());
//        Assertions.assertSame(1, spannedScope.getDogSymbolsWithSubKinds().size());
//        Assertions.assertSame(1, spannedScope.getCatSymbolsWithSubKinds().size());
//        Assertions.assertSame(1, spannedScope.getFishSymbolsWithSubKinds().size());
//        Assertions.assertSame(4,spannedScope.getAnimalSymbolsWithSubKinds().size());
//        Assertions.assertSame(11, spannedScope.getZooSymbolsWithSubKinds().size());
//
//        Assertions.assertEquals("carFordName",      spannedScope.getFordSymbolsWithSubKinds().values().get(0).getName());
//        Assertions.assertEquals("carVolvoName",     spannedScope.getVolvoSymbolsWithSubKinds().values().get(0).getName());
//
//        Assertions.assertEquals("carFordName",      spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTFord.class)).findAny().get().getName());
//        Assertions.assertEquals("carVolvoName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTVolvo.class)).findAny().get().getName());
//        Assertions.assertEquals("carTruckName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTTruck.class)).findAny().get().getName());
//
//        Assertions.assertEquals("carMazdaName",     spannedScope.getMazdaSymbolsWithSubKinds().values().get(0).getName());
//        Assertions.assertEquals("carBeatleName",    spannedScope.getBeatleSymbolsWithSubKinds().values().get(0).getName());
//
//        Assertions.assertEquals("carMazdaName",     spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTMazda.class)).findAny().get().getName());
//        Assertions.assertEquals("carBeatleName",    spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTBeatle.class)).findAny().get().getName());
//        Assertions.assertEquals("carCabrioletName", spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCabriolet.class)).findAny().get().getName());
//
//        Assertions.assertEquals("carFordName",      spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTFord.class)).findAny().get().getName());
//        Assertions.assertEquals("carVolvoName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTVolvo.class)).findAny().get().getName());
//        Assertions.assertEquals("carTruckName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTTruck.class)).findAny().get().getName());
//        Assertions.assertEquals("carMazdaName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTMazda.class)).findAny().get().getName());
//        Assertions.assertEquals("carBeatleName",    spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTBeatle.class)).findAny().get().getName());
//        Assertions.assertEquals("carCabrioletName", spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCabriolet.class)).findAny().get().getName());
//        Assertions.assertEquals("carCarName",       spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCar.class)).findAny().get().getName());
//
//        Assertions.assertEquals("animalDogName",    spannedScope.getDogSymbolsWithSubKinds().values().get(0).getName());
//        Assertions.assertEquals("animalCatName",    spannedScope.getCatSymbolsWithSubKinds().values().get(0).getName());
//        Assertions.assertEquals("animalFishName",   spannedScope.getFishSymbolsWithSubKinds().values().get(0).getName());
//
//        Assertions.assertEquals("animalDogName",    spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTDog.class)).findAny().get().getName());
//        Assertions.assertEquals("animalCatName",    spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCat.class)).findAny().get().getName());
//        Assertions.assertEquals("animalFishName",   spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTFish.class)).findAny().get().getName());
//        Assertions.assertEquals("animalAnimalName", spannedScope.getAnimalSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTAnimal.class)).findAny().get().getName());
//
//        Assertions.assertEquals("carFordName",      spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTFord.class)).findAny().get().getName());
//        Assertions.assertEquals("carVolvoName",     spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTVolvo.class)).findAny().get().getName());
//        Assertions.assertEquals("carTruckName",     spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTTruck.class)).findAny().get().getName());
//        Assertions.assertEquals("carMazdaName",     spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTMazda.class)).findAny().get().getName());
//        Assertions.assertEquals("carBeatleName",    spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTBeatle.class)).findAny().get().getName());
//        Assertions.assertEquals("carCabrioletName", spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCabriolet.class)).findAny().get().getName());
//        Assertions.assertEquals("carCarName",       spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCar.class)).findAny().get().getName());
//        Assertions.assertEquals("animalDogName",    spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTDog.class)).findAny().get().getName());
//        Assertions.assertEquals("animalCatName",    spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTCat.class)).findAny().get().getName());
//        Assertions.assertEquals("animalFishName",   spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTFish.class)).findAny().get().getName());
//        Assertions.assertEquals("animalAnimalName", spannedScope.getZooSymbolsWithSubKinds().values().stream().filter(m -> m.getAstNode().getClass().equals(mc.feature.symboltable.symbolswithsubkindswithmultipleparents._ast.ASTAnimal.class)).findAny().get().getName());
    }

    /**
     * This test ensures that all expected classes are generated. Otherwise, the test will not compile
     */
    @SuppressWarnings("unused")
    @Test
    public void test() {
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTZoo zoo = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTZooBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTSymbolsWithSubKindsNode.class, zoo);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTCar car = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTCarBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTVehicle.class, car);
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTZoo.class, car);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabriolet cabriolet = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabrioletBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCar.class, cabriolet);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTBeatle beatle = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTBeatleBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabriolet.class, beatle);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTMazda mazda = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTMazdaBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCabriolet.class, mazda);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruck truck = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruckBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTCar.class, truck);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTFord ford = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTFordBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruck.class, ford);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTVolvo volvo = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTVolvoBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTTruck.class, volvo);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTAnimal animal = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTAnimalBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTLeg.class, animal);
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTZoo.class, animal);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTDog dog = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTDogBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTAnimal.class, dog);
        mc.feature.symboltable.symbolswithsubkinds._ast.ASTCat cat = new mc.feature.symboltable.symbolswithsubkinds._ast.ASTCatBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(mc.feature.symboltable.symbolswithsubkinds._ast.ASTAnimal.class, cat);
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }
}
