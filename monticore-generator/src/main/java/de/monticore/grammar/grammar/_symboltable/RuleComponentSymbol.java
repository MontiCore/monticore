/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Optional;

public class RuleComponentSymbol extends RuleComponentSymbolTOP  {

//  private boolean isTerminal;
//
//  private boolean isNonterminal;
//
//  private boolean isConstantGroup;
//
//  private boolean isConstant;
//
//  private boolean isLexerNonterminal;
//
//  private List<String> supProds = Lists.newArrayList();

  //TODO: Remove if usageName is defined via symbolrule
  private String usageName = "";

  /**
   * Only for nonterminals. E.g., in u:R R is the name of the referenced prod.
   */
  private ProdSymbolReference referencedProd;

//  /**
//   * E.g., in from:Name@State State is the referenced symbol name
//   */
//  private Optional<String> referencedSymbolName = empty();

//  /**
//   * e.g., A* or A+
//   */
//  private boolean isList = false;

//  /**
//   * e.g., A?
//   */
//  private boolean isOptional = false;

  public RuleComponentSymbol(String name) {
    super(name);
    setSubProds(new ArrayList<>()); //TODO: Move to superclass
    setReferencedSymbolName(Optional.empty()); //TODO: Move to superclass
  }


//  public void setNonterminal(boolean nonterminal) {
//    isNonterminal = nonterminal;
//  }
//
//  public boolean isNonterminal() {
//    return isNonterminal;
//  }
//
//  public boolean isTerminal() {
//    return isTerminal;
//  }
//
//  public void setTerminal(boolean terminal) {
//    isTerminal = terminal;
//  }
//
//  public boolean isConstantGroup() {
//    return isConstantGroup;
//  }
//
//  public void setConstantGroup(boolean constantGroup) {
//    isConstantGroup = constantGroup;
//  }
//
//  public boolean isConstant() {
//    return isConstant;
//  }
//
//  public void setConstant(boolean constant) {
//    isConstant = constant;
//  }
//
//  public boolean isLexerNonterminal() {
//    return isLexerNonterminal;
//  }
//
//  public void setLexerNonterminal(boolean lexerNonterminal) {
//    isLexerNonterminal = lexerNonterminal;
//  }
//
//  /**
//   * @return true, if rule is used as a list, i.e. '+' or '*'.
//   */
//  public boolean isList() {
//    return isList;
//  }
//
//  /**
//   * @param isList true, if rule is used as a list, i.e. '+' or '*'.
//   */
//  public void setList(boolean isList) {
//    this.isList = isList;
//  }
//
//  /**
//   * @return true, if rule is optional, i.e. '?'.
//   */
//  public boolean isOptional() {
//    return isOptional;
//  }
//
//  /**
//   * @param isOptional true, if rule is optional, i.e. '?'.
//   */
//  public void setOptional(boolean isOptional) {
//    this.isOptional = isOptional;
//  }

  /**
   * E.g. usageName:QualifiedName
   *
   * @param usageName the usageName to set
   */
//  @Override //TODO: uncomment if usageName is defined via symbolrule
  public void setUsageName(String usageName) {
    this.usageName = nullToEmpty(usageName);
  }

  //TODO: Remove if usageName is defined via symbolrule
  public String getUsageName() {
    return this.usageName;
  }

  public void setReferencedProd(ProdSymbolReference referencedProd) {
    this.referencedProd = referencedProd;
  }

  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<ProdSymbolReference> getReferencedProd() {
    return ofNullable(referencedProd);
  }

//  /**
//   * @return the name of the symbol referenced by this component (via the
//   * <code>@</code> notation)
//   */
//  public Optional<String> getReferencedSymbolName() {
//    return referencedSymbolName;
//  }

//  @Override
  public void setReferencedSymbolName(String referencedSymbolName) {
    setReferencedSymbolName(ofNullable(emptyToNull(referencedSymbolName)));
  }

  public boolean isSymbolReference() {
    return getReferencedSymbolName().isPresent();
  }


  public void addSubProdComponent(String constantName) {
    getSubProds().add(constantName);
  }

//  public Collection<String> getSubProdComponents() {
//    return supProds;
//  }

}
