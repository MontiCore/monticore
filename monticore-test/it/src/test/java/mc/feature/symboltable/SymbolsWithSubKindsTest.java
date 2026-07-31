/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill;
import mc.feature.symboltable.symbolswithsubkinds._ast.*;
import mc.feature.symboltable.symbolswithsubkinds._parser.SymbolsWithSubKindsParser;
import mc.feature.symboltable.symbolswithsubkinds._symboltable.*;
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
    public void testSymbol() throws IOException {
        // parse model
        SymbolsWithSubKindsParser scopeAttributesParser = new SymbolsWithSubKindsParser();
        Optional<ASTLot> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st");
        Assertions.assertFalse(scopeAttributesParser.hasErrors());
        Assertions.assertTrue(astSup.isPresent());

        // create symboltable
        ISymbolsWithSubKindsGlobalScope globalScope = SymbolsWithSubKindsMill.globalScope();
        globalScope.setFileExt("st");
        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

        ISymbolsWithSubKindsScope scope = SymbolsWithSubKindsMill
                .scopesGenitorDelegator().createFromAST(astSup.get());

        Assertions.assertSame(1, scope.getSubScopes().size());
        ISymbolsWithSubKindsScope spannedScope =  scope.getSubScopes().getFirst();
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
        Assertions.assertSame(1, spannedScope.getMotorcycleSymbolsWithSubKinds().size());
        Assertions.assertSame(8, spannedScope.getVehicleSymbolsWithSubKinds().size());

        Assertions.assertEquals("carFordName",      spannedScope.getFordSymbolsWithSubKinds().values().getFirst().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getVolvoSymbolsWithSubKinds().values().getFirst().getName());

        Assertions.assertEquals("carFordName",      spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(FordSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(VolvoSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carTruckName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(TruckSymbol.class)).findAny().get().getName());

        Assertions.assertEquals("carMazdaName",     spannedScope.getMazdaSymbolsWithSubKinds().values().getFirst().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getBeatleSymbolsWithSubKinds().values().getFirst().getName());

        Assertions.assertEquals("carMazdaName",     spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MazdaSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(BeatleSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CabrioletSymbol.class)).findAny().get().getName());

        Assertions.assertEquals("carFordName",      spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(FordSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(VolvoSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carTruckName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(TruckSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carMazdaName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MazdaSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(BeatleSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CabrioletSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carCarName",       spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CarSymbol.class)).findAny().get().getName());

        Assertions.assertEquals("motorcycleName",   spannedScope.getMotorcycleSymbolsWithSubKinds().values().getFirst().getName());
      
      Assertions.assertEquals("carFordName",      spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(FordSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carVolvoName",     spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(VolvoSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carTruckName",     spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(TruckSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carMazdaName",     spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MazdaSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carBeatleName",    spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(BeatleSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carCabrioletName", spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CabrioletSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("carCarName",       spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CarSymbol.class)).findAny().get().getName());
        Assertions.assertEquals("motorcycleName",   spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MotorcycleSymbol.class)).findAny().get().getName());

        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    /**
     * This test ensures that all expected classes are generated. Otherwise, the test will not compile
     */
    @SuppressWarnings("unused")
    @Test
    public void test() {
        ASTCar car = new ASTCarBuilder().uncheckedBuild();
        Assertions.assertInstanceOf(ASTVehicle.class, car);
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

        Assertions.assertTrue(Log.getFindings().isEmpty());
    }
}
