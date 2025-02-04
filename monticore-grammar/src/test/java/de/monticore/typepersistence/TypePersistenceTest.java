/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typepersistence;

import de.monticore.io.paths.MCPath;
import de.monticore.typepersistence.variable.VariableMill;
import de.monticore.typepersistence.variable._ast.ASTVar;
import de.monticore.typepersistence.variable._parser.VariableParser;
import de.monticore.typepersistence.variable._symboltable.IVariableGlobalScope;
import de.monticore.typepersistence.variable._symboltable.IVariableScope;
import de.monticore.typepersistence.variable._symboltable.VariableScopesGenitorDelegator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class TypePersistenceTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    VariableMill.reset();
    VariableMill.init();
  }
  
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
    globalScope.setSymbolPath(new MCPath());
    globalScope.setFileExt("tp");

    //Parse blah model
    VariableParser blahParser = new VariableParser();
    Optional<ASTVar> varModel = blahParser.parse_String("var String a");
    VariableScopesGenitorDelegator varSymbolTableCreator = VariableMill.scopesGenitorDelegator();
    IVariableScope blahSymbolTable = varSymbolTableCreator.createFromAST(varModel.get());
    Assertions.assertTrue(varModel.isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
