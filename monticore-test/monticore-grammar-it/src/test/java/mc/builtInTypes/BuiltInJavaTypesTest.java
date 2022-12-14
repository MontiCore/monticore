/* (c) https://github.com/MontiCore/monticore */
package mc.builtInTypes;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import de.se_rwth.commons.logging.Log;

public class BuiltInJavaTypesTest {

  private static IOOSymbolsGlobalScope gs;

  @BeforeClass
  public static void setup(){
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    //use OOSymbolsGlobalScope to test if it also works for grammars that extend the BasicSymbols grammar
    gs = OOSymbolsMill.globalScope();
  }
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBuiltInPrimitiveJavaTypes(){
    //assert that the primitive types can be resolved in the scope
    Optional<TypeSymbol> intsymtype = gs.resolveType("int");
    Optional<TypeSymbol> doublesymtype = gs.resolveType("double");
    Optional<TypeSymbol> floatsymtype = gs.resolveType("float");
    Optional<TypeSymbol> longsymtype = gs.resolveType("long");
    Optional<TypeSymbol> charsymtype = gs.resolveType("char");
    Optional<TypeSymbol> shortsymtype = gs.resolveType("short");
    Optional<TypeSymbol> bytesymtype = gs.resolveType("byte");
    Optional<TypeSymbol> booleansymtype = gs.resolveType("boolean");

    assertTrue(intsymtype.isPresent());
    assertTrue(doublesymtype.isPresent());
    assertTrue(floatsymtype.isPresent());
    assertTrue(longsymtype.isPresent());
    assertTrue(charsymtype.isPresent());
    assertTrue(shortsymtype.isPresent());
    assertTrue(bytesymtype.isPresent());
    assertTrue(booleansymtype.isPresent());

    //assert that the primitives have no fields, methods, type variables and super types
    assertTrue(intsymtype.get().getFunctionList().isEmpty());
    assertTrue(intsymtype.get().getVariableList().isEmpty());
    assertTrue(intsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(intsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(doublesymtype.get().getFunctionList().isEmpty());
    assertTrue(doublesymtype.get().getVariableList().isEmpty());
    assertTrue(doublesymtype.get().getSuperTypesList().isEmpty());
    assertTrue(doublesymtype.get().getTypeParameterList().isEmpty());

    assertTrue(floatsymtype.get().getFunctionList().isEmpty());
    assertTrue(floatsymtype.get().getVariableList().isEmpty());
    assertTrue(floatsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(floatsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(longsymtype.get().getFunctionList().isEmpty());
    assertTrue(longsymtype.get().getVariableList().isEmpty());
    assertTrue(longsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(longsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(charsymtype.get().getFunctionList().isEmpty());
    assertTrue(charsymtype.get().getVariableList().isEmpty());
    assertTrue(charsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(charsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(shortsymtype.get().getFunctionList().isEmpty());
    assertTrue(shortsymtype.get().getVariableList().isEmpty());
    assertTrue(shortsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(shortsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(bytesymtype.get().getFunctionList().isEmpty());
    assertTrue(bytesymtype.get().getVariableList().isEmpty());
    assertTrue(bytesymtype.get().getSuperTypesList().isEmpty());
    assertTrue(bytesymtype.get().getTypeParameterList().isEmpty());

    assertTrue(booleansymtype.get().getFunctionList().isEmpty());
    assertTrue(booleansymtype.get().getVariableList().isEmpty());
    assertTrue(booleansymtype.get().getSuperTypesList().isEmpty());
    assertTrue(booleansymtype.get().getTypeParameterList().isEmpty());
  }


}
