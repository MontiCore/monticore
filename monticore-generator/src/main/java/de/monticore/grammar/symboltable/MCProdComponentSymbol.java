/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class MCProdComponentSymbol extends CommonScopeSpanningSymbol {
  
  public static final MCProdComponentKind KIND = new MCProdComponentKind();
  
  private boolean isTerminal;
  
  private boolean isNonterminal;
  
  private boolean isConstantGroup;
  
  private boolean isConstant;
  
  private boolean isLexerNonterminal;
  
  /**
   * E.g. usageName:QualifiedName
   */
  private String usageName = "";
  
  /**
   * Only for nonterminals. E.g., in u:R R is the name of the referenced prod.
   */
  private MCProdSymbolReference referencedProd;
  
  /**
   * E.g., in from:Name@State State is the referenced symbol name
   */
  private Optional<String> referencedSymbolName = Optional.empty();
  
  /**
   * e.g., A* or A+
   */
  private boolean isList = false;
  
  /**
   * e.g., A?
   */
  private boolean isOptional = false;
  
  public MCProdComponentSymbol(String name) {
    super(name, KIND);
  }
  
  public void setNonterminal(boolean nonterminal) {
    isNonterminal = nonterminal;
  }
  
  public boolean isNonterminal() {
    return isNonterminal;
  }
  
  public boolean isTerminal() {
    return isTerminal;
  }
  
  public void setTerminal(boolean terminal) {
    isTerminal = terminal;
  }
  
  public boolean isConstantGroup() {
    return isConstantGroup;
  }
  
  public void setConstantGroup(boolean constantGroup) {
    isConstantGroup = constantGroup;
  }
  
  public boolean isConstant() {
    return isConstant;
  }
  
  public void setConstant(boolean constant) {
    isConstant = constant;
  }
  
  public boolean isLexerNonterminal() {
    return isLexerNonterminal;
  }
  
  public void setLexerNonterminal(boolean lexerNonterminal) {
    isLexerNonterminal = lexerNonterminal;
  }
  
  /**
   * @return true, if rule is used as a list, i.e. '+' or '*'.
   */
  public boolean isList() {
    return isList;
  }
  
  /**
   * @param isList true, if rule is used as a list, i.e. '+' or '*'.
   */
  public void setList(boolean isList) {
    this.isList = isList;
  }
  
  /**
   * @return true, if rule is optional, i.e. '?'.
   */
  public boolean isOptional() {
    return isOptional;
  }
  
  /**
   * @param isOptional true, if rule is optional, i.e. '?'.
   */
  public void setOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }
  
  /**
   * E.g. usageName:QualifiedName
   *
   * @param usageName the usageName to set
   */
  public void setUsageName(String usageName) {
    this.usageName = nullToEmpty(usageName);
  }
  
  /**
   * @return usageName
   */
  public String getUsageName() {
    return this.usageName;
  }
  
  public void setReferencedProd(MCProdSymbolReference referencedProd) {
    this.referencedProd = referencedProd;
  }
  
  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<MCProdSymbolReference> getReferencedProd() {
    return Optional.ofNullable(referencedProd);
  }
  
  /**
   * @return the name of the symbol referenced by this component (via the
   * <code>@</code> notation)
   */
  public Optional<String> getReferencedSymbolName() {
    return referencedSymbolName;
  }
  
  public void setReferencedSymbolName(String referencedSymbolName) {
    this.referencedSymbolName = Optional.ofNullable(emptyToNull(referencedSymbolName));
  }
  
  public boolean isSymbolReference() {
    return referencedSymbolName.isPresent();
  }
  
  public Collection<MCProdComponentSymbol> getSubProdComponents() {
    return getSpannedScope().resolveLocally(MCProdComponentSymbol.KIND);
  }
  
  public Optional<MCProdComponentSymbol> getSubProdComponent(String componentName) {
    return getSpannedScope().resolveLocally(componentName, MCProdComponentSymbol.KIND);
  }
  
  public void addSubProdComponent(MCProdComponentSymbol prodComp) {
    Log.errorIfNull(prodComp);
    getMutableSpannedScope().add(prodComp);
  }
  
  public static class MCProdComponentKind implements SymbolKind {
    
    private static final String NAME = MCProdComponentKind.class.getName();
    
    protected MCProdComponentKind() {
    }
    
    @Override
    public String getName() {
      return NAME;
    }
    
    @Override
    public boolean isKindOf(SymbolKind kind) {
      return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
    }
    
  }
}
