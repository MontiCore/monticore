/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.symbols.basicsymbols;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.List;

public class BasicSymbolsMill extends BasicSymbolsMillTOP {

  protected static BasicSymbolsMill primitiveTypesInitializer;

  public static void initMe (BasicSymbolsMill a)  {
    mill = a;
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
    millBasicSymbolsTraverserImplementation = a;
    primitiveTypesInitializer = a;
  }

  public static final String INT = "int";

  public static final String DOUBLE = "double";

  public static final String FLOAT = "float";

  public static final String SHORT = "short";

  public static final String LONG = "long";

  public static final String BOOLEAN = "boolean";

  public static final String BYTE = "byte";

  public static final String CHAR = "char";

  public static final String NULL = "null";

  public static final String VOID = "void";

  public static final List<String> PRIMITIVE_LIST = Lists.newArrayList(INT, DOUBLE, FLOAT, SHORT, LONG, BOOLEAN, BYTE, CHAR);


  public static void initializePrimitives(){
    if(primitiveTypesInitializer == null){
      primitiveTypesInitializer = getMill();
    }
    primitiveTypesInitializer._initializePrimitives();
  }

  public void _initializePrimitives(){
    IBasicSymbolsGlobalScope gs = globalScope();

    for(String primitive: PRIMITIVE_LIST){
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
