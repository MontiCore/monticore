/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typepersistence;

import de.monticore.io.paths.ModelPath;
import de.monticore.typepersistence.variable.VariableMill;
import de.monticore.typepersistence.variable._ast.ASTVar;
import de.monticore.typepersistence.variable._parser.VariableParser;
import de.monticore.typepersistence.variable._symboltable.IVariableGlobalScope;
import de.monticore.typepersistence.variable._symboltable.IVariableScope;
import de.monticore.typepersistence.variable._symboltable.VariableScopesGenitorDelegator;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class TypePersistenceTest {

  @Test
  public void test() throws IOException {

    // infrastruktur aufbauen, modelle zum resolven einlesen, SymTab aufbauen, adapter schreiben, globalscope foo und blah verbinden
    // TransitiveAdapterResolvingFilter implementieren und im globscope registrieren,
    //
   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Blah/Blub Infrastruktur
    ******************************************************************************************************************
    */

    //Create global scope for our language combination
    IVariableGlobalScope globalScope = VariableMill
        .globalScope();
    globalScope.setModelPath(new ModelPath());
    globalScope.setFileExt("tp");

    //Parse blah model
    VariableParser blahParser = new VariableParser();
    Optional<ASTVar> varModel = blahParser.parse_String("var String a");
    VariableScopesGenitorDelegator varSymbolTableCreator = VariableMill.scopesGenitorDelegator();
    IVariableScope blahSymbolTable = varSymbolTableCreator.createFromAST(varModel.get());
    assertTrue(varModel.isPresent());
  }
}
