/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.symbols.basicsymbols;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.List;

public class BasicSymbolsMill extends BasicSymbolsMillTOP {

  protected static BasicSymbolsMill primitiveTypesInitializer;

  public static void initMe (BasicSymbolsMill a)  {
    mill = a;
    millBasicSymbolsDelegatorVisitorBuilder = a;
    millDiagramSymbolBuilder = a;
    millTypeSymbolBuilder = a;
    millTypeVarSymbolBuilder = a;
    millVariableSymbolBuilder = a;
    millFunctionSymbolBuilder = a;
    millDiagramSymbolSurrogateBuilder = a;
    millTypeSymbolSurrogateBuilder = a;
    millTypeVarSymbolSurrogateBuilder = a;
    millVariableSymbolSurrogateBuilder = a;
    millFunctionSymbolSurrogateBuilder = a;
    millBasicSymbolsSymbolTableCreatorDelegator = a;
    millBasicSymbolsSymbolTableCreator = a;
    millBasicSymbolsTraverserImplementation = a;
    primitiveTypesInitializer = a;
  }

  public static void initializePrimitives(){
    if(primitiveTypesInitializer == null){
      primitiveTypesInitializer = getMill();
    }
    primitiveTypesInitializer._initializePrimitives();
  }

  public void _initializePrimitives(){
    IBasicSymbolsGlobalScope gs = globalScope();

    List<String> primitives = Lists.newArrayList("int", "double", "float", "short", "long", "boolean", "byte", "char");

    for(String primitive: primitives){
      gs.add(createPrimitive(primitive));
    }
  }

  private TypeSymbol createPrimitive(String name){
    return typeSymbolBuilder()
            .setName(name)
            .setEnclosingScope(globalScope())
            .setFullName(name)
            .setSpannedScope(scope())
            .build();
  }


}
