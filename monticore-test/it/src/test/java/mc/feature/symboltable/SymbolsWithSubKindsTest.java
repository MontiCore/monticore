/* (c) https://github.com/MontiCore/monticore */

package mc.feature.symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.symboltable.symbolswithsubkinds.SymbolsWithSubKindsMill;
import mc.feature.symboltable.symbolswithsubkinds._ast.*;
import mc.feature.symboltable.symbolswithsubkinds._parser.SymbolsWithSubKindsParser;
import mc.feature.symboltable.symbolswithsubkinds._symboltable.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SymbolsWithSubKindsMill.class)
public class SymbolsWithSubKindsTest {

    @Test
    public void testSymbol() throws IOException {
        // parse model
        SymbolsWithSubKindsParser scopeAttributesParser = SymbolsWithSubKindsMill.parser();
        Optional<ASTLot> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st");
        assertFalse(scopeAttributesParser.hasErrors());
        assertTrue(astSup.isPresent());

        // create symboltable
        ISymbolsWithSubKindsGlobalScope globalScope = SymbolsWithSubKindsMill.globalScope();
        globalScope.setFileExt("st");
        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/symboltable"));

        ISymbolsWithSubKindsScope scope = SymbolsWithSubKindsMill
                .scopesGenitorDelegator().createFromAST(astSup.get());

        assertSame(1, scope.getSubScopes().size());
        ISymbolsWithSubKindsScope spannedScope =  scope.getSubScopes().getFirst();
        assertInstanceOf(SymbolsWithSubKindsScope.class , spannedScope);

        // check if the methods return the correct number of symbols
        // see monticore-test/it/src/test/resources/mc/feature/symboltable/SymbolsWithSubKinds.st
        assertSame(1, spannedScope.getFordSymbolsWithSubKinds().size());
        assertSame(1, spannedScope.getVolvoSymbolsWithSubKinds().size());
        assertSame(3,spannedScope.getTruckSymbolsWithSubKinds().size());
        assertSame(1,spannedScope.getBeatleSymbolsWithSubKinds().size());
        assertSame(1, spannedScope.getMazdaSymbolsWithSubKinds().size());
        assertSame(3,spannedScope.getCabrioletSymbolsWithSubKinds().size());
        assertSame(7,spannedScope.getCarSymbolsWithSubKinds().size());
        assertSame(1, spannedScope.getMotorcycleSymbolsWithSubKinds().size());
        assertSame(8, spannedScope.getVehicleSymbolsWithSubKinds().size());

        assertEquals("carFordName",      spannedScope.getFordSymbolsWithSubKinds().values().getFirst().getName());
        assertEquals("carVolvoName",     spannedScope.getVolvoSymbolsWithSubKinds().values().getFirst().getName());

        assertEquals("carFordName",      spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(FordSymbol.class)).findAny().get().getName());
        assertEquals("carVolvoName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(VolvoSymbol.class)).findAny().get().getName());
        assertEquals("carTruckName",     spannedScope.getTruckSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(TruckSymbol.class)).findAny().get().getName());

        assertEquals("carMazdaName",     spannedScope.getMazdaSymbolsWithSubKinds().values().getFirst().getName());
        assertEquals("carBeatleName",    spannedScope.getBeatleSymbolsWithSubKinds().values().getFirst().getName());

        assertEquals("carMazdaName",     spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MazdaSymbol.class)).findAny().get().getName());
        assertEquals("carBeatleName",    spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(BeatleSymbol.class)).findAny().get().getName());
        assertEquals("carCabrioletName", spannedScope.getCabrioletSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CabrioletSymbol.class)).findAny().get().getName());

        assertEquals("carFordName",      spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(FordSymbol.class)).findAny().get().getName());
        assertEquals("carVolvoName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(VolvoSymbol.class)).findAny().get().getName());
        assertEquals("carTruckName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(TruckSymbol.class)).findAny().get().getName());
        assertEquals("carMazdaName",     spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MazdaSymbol.class)).findAny().get().getName());
        assertEquals("carBeatleName",    spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(BeatleSymbol.class)).findAny().get().getName());
        assertEquals("carCabrioletName", spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CabrioletSymbol.class)).findAny().get().getName());
        assertEquals("carCarName",       spannedScope.getCarSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CarSymbol.class)).findAny().get().getName());

        assertEquals("motorcycleName",   spannedScope.getMotorcycleSymbolsWithSubKinds().values().getFirst().getName());
      
      assertEquals("carFordName",      spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(FordSymbol.class)).findAny().get().getName());
        assertEquals("carVolvoName",     spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(VolvoSymbol.class)).findAny().get().getName());
        assertEquals("carTruckName",     spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(TruckSymbol.class)).findAny().get().getName());
        assertEquals("carMazdaName",     spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MazdaSymbol.class)).findAny().get().getName());
        assertEquals("carBeatleName",    spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(BeatleSymbol.class)).findAny().get().getName());
        assertEquals("carCabrioletName", spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CabrioletSymbol.class)).findAny().get().getName());
        assertEquals("carCarName",       spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(CarSymbol.class)).findAny().get().getName());
        assertEquals("motorcycleName",   spannedScope.getVehicleSymbolsWithSubKinds().values().stream().filter(m -> m.getClass().equals(MotorcycleSymbol.class)).findAny().get().getName());
    }

    /**
     * This test ensures that all expected classes are generated. Otherwise, the test will not compile
     */
    @SuppressWarnings("unused")
    @Test
    public void test() {
        ASTCar car = new ASTCarBuilder().uncheckedBuild();
        assertInstanceOf(ASTVehicle.class, car);
        ASTCabriolet cabriolet = new ASTCabrioletBuilder().uncheckedBuild();
        assertInstanceOf(ASTCar.class, cabriolet);
        ASTBeatle beatle = new ASTBeatleBuilder().uncheckedBuild();
        assertInstanceOf(ASTCabriolet.class, beatle);
        ASTMazda mazda = new ASTMazdaBuilder().uncheckedBuild();
        assertInstanceOf(ASTCabriolet.class, mazda);
        ASTTruck truck = new ASTTruckBuilder().uncheckedBuild();
        assertInstanceOf(ASTCar.class, truck);
        ASTFord ford = new ASTFordBuilder().uncheckedBuild();
        assertInstanceOf(ASTTruck.class, ford);
        ASTVolvo volvo = new ASTVolvoBuilder().uncheckedBuild();
        assertInstanceOf(ASTTruck.class, volvo);
    }
}
