/* (c) https://github.com/MontiCore/monticore */
package mc.builtInTypes;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import de.se_rwth.commons.logging.Log;

public class BuiltInJavaTypesTest {

  private static IOOSymbolsGlobalScope gs;

  @BeforeAll
  public static void setup(){
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    //use OOSymbolsGlobalScope to test if it also works for grammars that extend the BasicSymbols grammar
    gs = OOSymbolsMill.globalScope();
  }
  
  @BeforeEach
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

    Assertions.assertTrue(intsymtype.isPresent());
    Assertions.assertTrue(doublesymtype.isPresent());
    Assertions.assertTrue(floatsymtype.isPresent());
    Assertions.assertTrue(longsymtype.isPresent());
    Assertions.assertTrue(charsymtype.isPresent());
    Assertions.assertTrue(shortsymtype.isPresent());
    Assertions.assertTrue(bytesymtype.isPresent());
    Assertions.assertTrue(booleansymtype.isPresent());

    //assert that the primitives have no fields, methods, type variables and super types
    Assertions.assertTrue(intsymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(intsymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(intsymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(intsymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(doublesymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(doublesymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(doublesymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(doublesymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(floatsymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(floatsymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(floatsymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(floatsymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(longsymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(longsymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(longsymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(longsymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(charsymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(charsymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(charsymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(charsymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(shortsymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(shortsymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(shortsymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(shortsymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(bytesymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(bytesymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(bytesymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(bytesymtype.get().getTypeParameterList().isEmpty());

    Assertions.assertTrue(booleansymtype.get().getFunctionList().isEmpty());
    Assertions.assertTrue(booleansymtype.get().getVariableList().isEmpty());
    Assertions.assertTrue(booleansymtype.get().getSuperTypesList().isEmpty());
    Assertions.assertTrue(booleansymtype.get().getTypeParameterList().isEmpty());
  }


}
