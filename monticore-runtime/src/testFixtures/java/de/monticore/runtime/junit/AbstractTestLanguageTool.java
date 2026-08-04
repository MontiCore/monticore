// (c) https://github.com/MontiCore/monticore
package de.monticore.runtime.junit;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IArtifactScope;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A wrapper around the language tool (parser, symbol table, CoCos)
 * to be used in tests.
 * This class contains the most common checks
 * (e.g., no findings in the {@link de.se_rwth.commons.logging.Log}).
 * Mostly, it simply delegates to the corresponding methods
 * of the language tool.
 * <p>
 * This class is intended to
 *
 * @param <N>  The ASTNode type representing the models
 * @param <AS> The type of ArtifactScope of the models
 */
public abstract class AbstractTestLanguageTool<
    N extends ASTNode,
    AS extends IArtifactScope
    > {

  /**
   * The main entry point; Does it all.
   * The String is parsed, the symbol table is created and completed.
   * Additionally, CoCos are checked.
   * <p>
   * This method can be used by any test
   * which expects to use valid models.
   *
   * @param modelStr the model as String
   * @return the AST with corresponding symbol table
   */
  public N getASTWithSymbolTable(String modelStr) {
    N ast = parse(modelStr);
    addSymbolTableToAST(ast);
    return ast;
  }

  /**
   * Does it all, except parsing;
   * Adds the symbol table and checks CoCos to a given AST.
   *
   * @param ast the model
   * @return the ArtifactScope created for the model
   */
  public AS addSymbolTableToAST(N ast) {
    runBetween_parse_createSymbolTable(ast);
    AS artifactScope = createSymbolTable(ast);
    runBetween_createSymbolTable_completeSymbolTable(ast);
    completeSymbolTable(ast);
    runPost_completeSymbolTable(ast);
    return artifactScope;
  }

  /**
   * Parses the model. Simply delegate to
   * {@code MyLangMill.parser().parse_String()}
   *
   * @param modelStr the model as a String
   * @return the parsed AST
   */
  public N parse(String modelStr) {
    Optional<N> astOpt;
    try {
      astOpt = _parse(modelStr);
    }
    catch (IOException e) {
      fail("Failed to parse input, exception occurred", e);
      return null;
    }
    assertNoFindings();
    assertNotNull(astOpt);
    assertTrue(astOpt.isPresent());
    return astOpt.get();
  }

  /**
   * Runs between {@link #parse(String)} and
   * {@link #createSymbolTable(ASTNode)}.
   * <p>
   * This method can be used, e.g., to run post-parse CoCos/Trafos.
   *
   * @param ast the model
   */
  public void runBetween_parse_createSymbolTable(N ast) {
    _runBetween_parse_createSymbolTable(ast);
    assertNoFindings();
  }

  /**
   * Creates the symbol table. Simply delegate to
   * {@code MyLangMill.scopesGenitorDelegator().createFromAST(ast)}.
   *
   * @param ast the model
   * @return the created ArtifactScope
   */
  public AS createSymbolTable(N ast) {
    AS artifactScope = _createSymbolTable(ast);
    assertNoFindings();
    assertNotNull(artifactScope);
    return artifactScope;
  }

  /**
   * Runs between {@link #createSymbolTable(ASTNode)}
   * and {@link #completeSymbolTable(ASTNode)}.
   *
   * @param ast the model
   */
  public void runBetween_createSymbolTable_completeSymbolTable(N ast) {
    _runBetween_createSymbolTable_completeSymbolTable(ast);
    assertNoFindings();
  }

  /**
   * Completes the symbol table.
   * Simply delegate to {@code MyLangTool::completeSymbolTable}.
   *
   * @param ast the model
   */
  public void completeSymbolTable(N ast) {
    _completeSymbolTable(ast);
    assertNoFindings();
  }

  /**
   * To be run after {@link #completeSymbolTable(ASTNode)}.
   * Usually contains running (most) CoCos.
   * <p>
   * Consider delegating to {@code MyLangTool::runDefaultCoCos}.
   *
   * @param ast the model
   */
  public void runPost_completeSymbolTable(N ast) {
    _runPost_completeSymbolTable(ast);
    assertNoFindings();
  }

  // HookPoints calling the default Tool methods

  abstract protected Optional<N> _parse(String modelStr) throws IOException;

  abstract protected AS _createSymbolTable(N ast);

  abstract protected void _completeSymbolTable(N ast);

  // HookPoints — Optional

  protected void _runBetween_parse_createSymbolTable(N ast) {
  }

  protected void _runBetween_createSymbolTable_completeSymbolTable(N ast) {
  }

  protected void _runPost_completeSymbolTable(N ast) {
  }

}
