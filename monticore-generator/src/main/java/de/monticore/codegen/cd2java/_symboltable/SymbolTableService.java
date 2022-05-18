/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.NODE_SUFFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.se_rwth.commons.Names.getSimpleName;

public class SymbolTableService extends AbstractService<SymbolTableService> {

  public SymbolTableService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public SymbolTableService(DiagramSymbol cdSymbol) {
    super(cdSymbol);
  }

  /**
   * overwrite methods of AbstractService to add the correct '_symboltbale' package for Symboltable generation
   */

  @Override
  public String getSubPackage() {
    return SYMBOL_TABLE_PACKAGE;
  }

  @Override
  protected SymbolTableService createService(DiagramSymbol cdSymbol) {
    return createSymbolTableService(cdSymbol);
  }

  public static SymbolTableService createSymbolTableService(DiagramSymbol cdSymbol) {
    return new SymbolTableService(cdSymbol);
  }

  public String getSerializationPackage(DiagramSymbol cdDefinitionSymbol) {
    // can be used to change the package that the serialization is generated to.
    // currently, it is generated into the same package as the remaining symboltable infrastructure
    return getPackage(cdDefinitionSymbol);
  }

  /**
   * scope class names e.g. AutomataScope
   */

  public String getScopeClassSimpleName() {
    return getScopeClassSimpleName(getCDSymbol());
  }

  public String getScopeClassSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SCOPE_SUFFIX;
  }

  public String getScopeClassFullName() {
    return getScopeClassFullName(getCDSymbol());
  }

  public String getScopeClassFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getScopeClassSimpleName(cdSymbol);
  }

  public ASTMCQualifiedType getScopeType() {
    return getMCTypeFacade().createQualifiedType(getScopeClassFullName());
  }

  /**
   * scope interface names e.g. IAutomataScope
   */

  public String getScopeInterfaceSimpleName() {
    return getScopeInterfaceSimpleName(getCDSymbol());
  }

  public String getScopeInterfaceSimpleName(DiagramSymbol cdSymbol) {
    return INTERFACE_PREFIX + cdSymbol.getName() + SCOPE_SUFFIX;
  }

  public String getScopeInterfaceFullName() {
    return getScopeInterfaceFullName(getCDSymbol());
  }

  public String getScopeInterfaceFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getScopeInterfaceSimpleName(cdSymbol);
  }

  public ASTMCQualifiedType getScopeInterfaceType() {
    return getScopeInterfaceType(getCDSymbol());
  }

  public ASTMCQualifiedType getScopeInterfaceType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getScopeInterfaceFullName(cdSymbol));
  }

  /**
   * artifact scope class names e.g. AutomataArtifactScope
   */

  public String getArtifactScopeSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + ARTIFACT_PREFIX + SCOPE_SUFFIX;
  }

  public String getArtifactScopeSimpleName() {
    return getArtifactScopeSimpleName(getCDSymbol());
  }

  public String getArtifactScopeFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getArtifactScopeSimpleName(cdSymbol);
  }

  public String getArtifactScopeFullName() {
    return getArtifactScopeFullName(getCDSymbol());
  }

  public ASTMCQualifiedType getArtifactScopeType() {
    return getMCTypeFacade().createQualifiedType(getArtifactScopeFullName());
  }

  /**
   * artifact scope interface names e.g. IAutomataArtifactScope
   */

  public String getArtifactScopeInterfaceSimpleName(DiagramSymbol cdSymbol) {
    return INTERFACE_PREFIX + cdSymbol.getName() + ARTIFACT_PREFIX + SCOPE_SUFFIX;
  }

  public String getArtifactScopeInterfaceSimpleName() {
    return getArtifactScopeInterfaceSimpleName(getCDSymbol());
  }

  public String getArtifactScopeInterfaceFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getArtifactScopeInterfaceSimpleName(cdSymbol);
  }

  public String getArtifactScopeInterfaceFullName() {
    return getArtifactScopeInterfaceFullName(getCDSymbol());
  }

  public ASTMCQualifiedType getArtifactScopeInterfaceType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getArtifactScopeInterfaceFullName(cdSymbol));
  }

  public ASTMCQualifiedType getArtifactScopeInterfaceType() {
    return getArtifactScopeInterfaceType(getCDSymbol());
  }

  /**
   * global scope class names e.g. AutomataGlobalScope
   */

  public String getGlobalScopeFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getGlobalScopeSimpleName(cdSymbol);
  }

  public String getGlobalScopeFullName() {
    return getGlobalScopeFullName(getCDSymbol());
  }

  public String getGlobalScopeSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + GLOBAL_SUFFIX + SCOPE_SUFFIX;
  }

  public String getGlobalScopeSimpleName() {
    return getGlobalScopeSimpleName(getCDSymbol());
  }


  /**
   * global scope interface names e.g. IAutomataGlobalScope
   */

  public String getGlobalScopeInterfaceFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getGlobalScopeInterfaceSimpleName(cdSymbol);
  }

  public String getGlobalScopeInterfaceFullName() {
    return getGlobalScopeInterfaceFullName(getCDSymbol());
  }

  public String getGlobalScopeInterfaceSimpleName(DiagramSymbol cdSymbol) {
    return INTERFACE_PREFIX + cdSymbol.getName() + GLOBAL_SUFFIX + SCOPE_SUFFIX;
  }

  public String getGlobalScopeInterfaceSimpleName() {
    return getGlobalScopeInterfaceSimpleName(getCDSymbol());
  }

  public ASTMCQualifiedType getGlobalScopeInterfaceType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getGlobalScopeInterfaceFullName(cdSymbol));
  }

  public ASTMCQualifiedType getGlobalScopeInterfaceType() {
    return getGlobalScopeInterfaceType(getCDSymbol());
  }

  /**
   * symbol reference class names e.g. AutomatonSymbolReference
   */

  public String getSymbolSurrogateFullName(ASTCDType astcdType, DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymbolSurrogateSimpleName(astcdType);
  }

  public String getSymbolSurrogateFullName(ASTCDType astcdType) {
    return getSymbolSurrogateFullName(astcdType, getCDSymbol());
  }

  public String getSymbolSurrogateSimpleName(ASTCDType astcdType) {
    return getSymbolSimpleName(astcdType) + SURROGATE_SUFFIX;
  }

  /**
   * symbol builder class name e.g. AutomatonSymbolSurrogateBuilder
   */
  public String getSymbolSurrogateBuilderSimpleName(ASTCDType astcdType) {
    return getSymbolSurrogateSimpleName(astcdType) + BUILDER_SUFFIX;
  }

  public String getSymbolSurrogateBuilderFullName(ASTCDType astcdType, DiagramSymbol cdDefinitionSymbol) {
    return getSymbolSurrogateFullName(astcdType, cdDefinitionSymbol) + BUILDER_SUFFIX;
  }

  public String getSymbolSurrogateBuilderFullName(ASTCDType astcdType) {
    return getSymbolSurrogateBuilderFullName(astcdType, getCDSymbol());
  }

  /**
   * resolving delegate symbol interface e.g. IAutomatonSymbolResolver
   */

  public String getSymbolResolverInterfaceSimpleName(ASTCDType astcdType) {
    return INTERFACE_PREFIX + getSymbolSimpleName(astcdType) + RESOLVER_SUFFIX;
  }

  public String getSymbolResolverInterfaceFullName(ASTCDType astcdType) {
    return getSymbolResolverInterfaceFullName(astcdType, getCDSymbol());
  }

  public String getSymbolResolverInterfaceFullName(ASTCDType astcdType, DiagramSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getSymbolResolverInterfaceSimpleName(astcdType);
  }

  /**
   * common symbol interface names e.g. ICommonAutomataSymbol
   */

  public String getCommonSymbolInterfaceSimpleName(DiagramSymbol cdSymbol) {
    return INTERFACE_PREFIX + COMMON_PREFIX + cdSymbol.getName() + SYMBOL_SUFFIX;
  }

  public String getCommonSymbolInterfaceSimpleName() {
    return getCommonSymbolInterfaceSimpleName(getCDSymbol());
  }

  public String getCommonSymbolInterfaceFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getCommonSymbolInterfaceSimpleName();
  }

  public String getCommonSymbolInterfaceFullName() {
    return getCommonSymbolInterfaceFullName(getCDSymbol());
  }

  /**
   * symbol table symbol interface names e.g. AutomataSymbolTable
   */

  public String getSymbolTableCreatorSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SYMBOL_TABLE_CREATOR_SUFFIX;
  }

  public String getSymbolTableCreatorSimpleName() {
    return getSymbolTableCreatorSimpleName(getCDSymbol());
  }

  public String getSymbolTableCreatorFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymbolTableCreatorSimpleName(cdSymbol);
  }

  public String getSymbolTableCreatorFullName() {
    return getSymbolTableCreatorFullName(getCDSymbol());
  }

  /**
   * symbol table symbol interface names e.g. AutomataSymbolTable
   */

  public String getSymbolTableCreatorDelegatorSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SYMBOL_TABLE_CREATOR_SUFFIX + DELEGATOR_SUFFIX;
  }

  public String getSymbolTableCreatorDelegatorSimpleName() {
    return getSymbolTableCreatorDelegatorSimpleName(getCDSymbol());
  }

  public String getSymbolTableCreatorDelegatorFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymbolTableCreatorDelegatorSimpleName(cdSymbol);
  }

  public String getSymbolTableCreatorDelegatorFullName() {
    return getSymbolTableCreatorDelegatorFullName(getCDSymbol());
  }

  /**
   * PhasedSymbolTableCreatorDelegator Names, e.g. AutomatonPhasedSymbolTableCreatorDelegator
   */

  public String getPhasedSymbolTableCreatorDelegatorSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + PHASED_SUFFIX + SYMBOL_TABLE_CREATOR_SUFFIX + DELEGATOR_SUFFIX;
  }

  public String getPhasedSymbolTableCreatorDelegatorSimpleName() {
    return getPhasedSymbolTableCreatorDelegatorSimpleName(getCDSymbol());
  }

  public String getPhasedSymbolTableCreatorDelegatorFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getPhasedSymbolTableCreatorDelegatorSimpleName(cdSymbol);
  }

  public String getPhasedSymbolTableCreatorDelegatorFullName() {
    return getPhasedSymbolTableCreatorDelegatorFullName(getCDSymbol());
  }

  /**
   * ScopeSkeletonCreatorDelegator Names e.g. AutomatonScopeSkeletonCreatorDelegator
   */

  public String getScopesGenitorDelegatorSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SCOPES_GENITOR_SUFFIX + DELEGATOR_SUFFIX;
  }

  public String getScopesGenitorDelegatorSimpleName() {
    return getScopesGenitorDelegatorSimpleName(getCDSymbol());
  }

  public String getScopesGenitorDelegatorFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getScopesGenitorDelegatorSimpleName(cdSymbol);
  }

  public String getScopesGenitorDelegatorFullName() {
    return getScopesGenitorDelegatorFullName(getCDSymbol());
  }

  /**
   * ScopeSkeletonCreator Names e.g. AutomatonScopeSkeletonCreator
   */

  public String getScopesGenitorSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SCOPES_GENITOR_SUFFIX;
  }

  public String getScopesGenitorSimpleName() {
    return getScopesGenitorSimpleName(getCDSymbol());
  }

  public String getScopesGenitorFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getScopesGenitorSimpleName(cdSymbol);
  }

  public String getScopesGenitorFullName() {
    return getScopesGenitorFullName(getCDSymbol());
  }

  /**
   * deser class names e.g. AutomataDeSer
   */

  public String getScopeDeSerSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + DE_SER_SUFFIX;
  }

  public String getScopeDeSerSimpleName() {
    return getScopeDeSerSimpleName(getCDSymbol());
  }

  public String getScopeDeSerFullName(DiagramSymbol cdSymbol) {
    return getSerializationPackage(cdSymbol) + "." + getScopeDeSerSimpleName(cdSymbol);
  }

  public String getScopeDeSerFullName() {
    return getScopeDeSerFullName(getCDSymbol());
  }


  /**
   * symTabMill interface names e.g. AutomataSymTabMill
   */

  public String getSymTabMillSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SYM_TAB_MILL_SUFFIX;
  }

  public String getSymTabMillFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSymTabMillSimpleName(cdSymbol);
  }

  /**
   * symTabMill interface names e.g. AutomataSymTabMill
   */

  public String getSuperSTCForSubSTCSimpleName(DiagramSymbol superCDSymbol, DiagramSymbol subCDSymbol) {
    return String.format(STC_FOR, superCDSymbol.getName(), subCDSymbol.getName());
  }

  public String getSuperSTCForSubSTCSimpleName(DiagramSymbol superCDSymbol) {
    return getSuperSTCForSubSTCSimpleName(superCDSymbol, getCDSymbol());
  }

  public String getSuperSTCForSubSTCFullName(DiagramSymbol superCDSymbol, DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getSuperSTCForSubSTCSimpleName(superCDSymbol, cdSymbol);
  }

  public String getSuperSTCForSubSTCFullName(DiagramSymbol superCDSymbol) {
    return getSuperSTCForSubSTCFullName(superCDSymbol, getCDSymbol());
  }

  /**
   * symbolDeSer class names e.g. StateSymbolDeSer
   */

  public String getSymbolDeSerFullName(ASTCDType astcdType, DiagramSymbol cdSymbol) {
    return getSerializationPackage(cdSymbol) + "." + getSymbolDeSerSimpleName(astcdType);
  }

  public String getSymbolDeSerFullName(ASTCDType astcdType) {
    return getSymbolDeSerFullName(astcdType, getCDSymbol());
  }

  public String getSymbolDeSerSimpleName(ASTCDType astcdType) {
    return getSymbolSimpleName(astcdType) + DE_SER_SUFFIX;
  }

  /**
   * symTabMill interface names e.g. AutomataSymTabMill
   */

  public String getSymbols2JsonSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + SYMBOLS_2_JSON_SUFFIX;
  }

  public String getSymbols2JsonSimpleName() {
    return getSymbols2JsonSimpleName(getCDSymbol());
  }

  public String getSymbols2JsonFullName(DiagramSymbol cdSymbol) {
    return getSerializationPackage(cdSymbol) + "." + getSymbols2JsonSimpleName(cdSymbol);
  }

  public String getSymbols2JsonFullName() {
    return getSymbols2JsonFullName(getCDSymbol());
  }

  public ASTMCQualifiedType getJsonPrinterType(){
    return getMCTypeFacade().createQualifiedType("de.monticore.symboltable.serialization.JsonPrinter");
  }


  /**
   * symbol class names e.g. AutomatonSymbol
   */

  public String getNameWithSymbolSuffix(ASTCDType clazz) {
    // normal symbol name calculation from -> does not consider manually given symbol types
    // e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;' will be MCQualifiedTypeSymbol
    return removeASTPrefix(clazz) + SYMBOL_SUFFIX;
  }

  public String getSymbolSimpleName(ASTCDType clazz) {
    // if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    // this will evaluate to MCTypeSymbol
    if (!hasSymbolStereotype(clazz.getModifier())) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifier());
      if (symbolTypeValue.isPresent()) {
        return getSimpleName(symbolTypeValue.get());
      }
    }
    return getNameWithSymbolSuffix(clazz);
  }

  public String getSymbolFullName(ASTCDType clazz) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    return getSymbolFullName(clazz, getCDSymbol());
  }

  public String getSymbolFullName(ASTCDType clazz, DiagramSymbol cdDefinitionSymbol) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (!hasSymbolStereotype(clazz.getModifier())) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifier());
      if (symbolTypeValue.isPresent()) {
        return symbolTypeValue.get();
      }
    }
    return getPackage(cdDefinitionSymbol) + "." + getNameWithSymbolSuffix(clazz);
  }

  public Optional<String> getDefiningSymbolFullName(ASTCDType clazz) {
    // does only return symbol defining parts, not parts with e.g. symbol (MCType)
    return getDefiningSymbolFullName(clazz, getCDSymbol());
  }

  public Optional<String> getDefiningSymbolFullName(ASTCDType clazz, DiagramSymbol cdDefinitionSymbol) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (hasSymbolStereotype(clazz.getModifier())) {
      return Optional.of(getPackage(cdDefinitionSymbol) + "." + getNameWithSymbolSuffix(clazz));
    }

    // no symbol at all
    return Optional.empty();
  }

  public Optional<String> getDefiningSymbolSimpleName(ASTCDType clazz) {
    // does only return symbol defining parts, not parts with e.g. symbol (MCType)
    if (hasSymbolStereotype(clazz.getModifier())) {
      // is a defining symbol
      return Optional.ofNullable(getNameWithSymbolSuffix(clazz));
    }

    // no symbol at all
    return Optional.empty();
  }

  public String getSimpleNameFromSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).lastIndexOf(SYMBOL_SUFFIX));
  }

  /**
   * Computes a set of all symbol defining rules in a class diagram, stored as
   * their qualified names.
   *
   * @param cdSymbol The input symbol of a class diagram
   * @return The set of symbol names within the class diagram
   */
  public Set<String> retrieveSymbolNamesFromCD(DiagramSymbol cdSymbol) {
    Set<String> symbolNames = new LinkedHashSet<>();
    // get AST for symbol
    ASTCDDefinition astcdDefinition = (ASTCDDefinition) cdSymbol.getAstNode();
    // add symbol definitions from interfaces
    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfacesList()) {
      if (hasSymbolStereotype(astcdInterface.getModifier())) {
        symbolNames.add(getSymbolFullName(astcdInterface, cdSymbol));
      }
    }
    // add symbol definitions from nonterminals
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassesList()) {
      if (hasSymbolStereotype(astcdClass.getModifier())) {
        symbolNames.add(getSymbolFullName(astcdClass, cdSymbol));
      }
    }
    return symbolNames;
  }

  /**
   * Computes the MCQualifiedType of the symbol from its corresponding CD type.
   *
   * @param node The input ASTCDType. Either a class or interface
   * @return The qualified type of the symbol as MCQualifiedType
   */
  public ASTMCQualifiedType getSymbolTypeFromAstType(ASTCDType node) {
    return getMCTypeFacade().createQualifiedType(getSymbolFullName(node));
  }

  /**
   * symbol builder class name e.g. AutomatonSymbolBuilder
   */
  public String getSymbolBuilderSimpleName(ASTCDType astcdType) {
    return getSymbolSimpleName(astcdType) + BUILDER_SUFFIX;
  }

  public String getSymbolBuilderFullName(ASTCDType astcdType, DiagramSymbol cdDefinitionSymbol) {
    return getSymbolFullName(astcdType, cdDefinitionSymbol) + BUILDER_SUFFIX;
  }

  public String getSymbolBuilderFullName(ASTCDType astcdType) {
    return getSymbolBuilderFullName(astcdType, getCDSymbol());
  }

  public String getReferencedSymbolTypeName(ASTCDAttribute attribute) {
    return getStereotypeValues(attribute, MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);
  }

  public boolean isReferencedSymbol(ASTCDAttribute attribute) {
    return hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL);
  }

  /**
   * returns the stereotype value for 'symbol' or 'inheritedSymbol'
   * only returns a value if it is a symbol reference and no symbol definition
   */
  public Optional<String> getSymbolTypeValue(ASTModifier modifier) {
    List<String> symbolStereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.SYMBOL);
    if (!symbolStereotypeValues.isEmpty()) {
      return Optional.ofNullable(symbolStereotypeValues.get(0));
    } else {
      List<String> inheritedStereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.INHERITED_SYMBOL);
      if (!inheritedStereotypeValues.isEmpty()) {
        return Optional.ofNullable(inheritedStereotypeValues.get(0));
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> getTypeWithSymbolInfo(ASTCDType type) {
    if (hasSymbolStereotype(type.getModifier())) {
      return Optional.of(type);
    }
    if (!type.isPresentSymbol()) {
      return Optional.empty();
    }

    List<CDTypeSymbol> superInterfaces = type.getSymbol().getSuperTypesList().stream()
            .map(ste -> ste.getTypeInfo())
            .map(ti -> ti.getName())
            .map(n -> resolveCDType(n))
            .filter(st -> st.isIsInterface())
            .collect(Collectors.toList());

    for (CDTypeSymbol superType : superInterfaces) {
      Optional<ASTCDType> result = getTypeWithSymbolInfo(superType.getAstNode());
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }


  /**
   * get classes and interfaces with scope or symbol stereotype
   */

  public List<ASTCDType> getSymbolDefiningProds(ASTCDDefinition astcdDefinition) {
    List<ASTCDType> symbolProds = getSymbolDefiningProds(astcdDefinition.getCDClassesList());
    symbolProds.addAll(getSymbolDefiningProds(astcdDefinition.getCDInterfacesList()));
    return symbolProds;
  }

  public List<ASTCDType> getSymbolDefiningSuperProds() {
    List<ASTCDType> symbolProds = new ArrayList<>();
    for (DiagramSymbol cdDefinitionSymbol : getSuperCDsTransitive()) {
      for (CDTypeSymbol type : getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && hasSymbolStereotype(type.getAstNode().getModifier())) {
          symbolProds.add(type.getAstNode());
        }
      }
    }
    return symbolProds;
  }

  public List<ASTCDType> getSymbolDefiningSuperProds(DiagramSymbol symbol) {
    List<ASTCDType> symbolProds = new ArrayList<>();
    for (DiagramSymbol cdDefinitionSymbol : getSuperCDsTransitive(symbol)) {
      for (CDTypeSymbol type : getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && hasSymbolStereotype(type.getAstNode().getModifier())) {
          symbolProds.add(type.getAstNode());
        }
      }
    }
    return symbolProds;
  }

  public List<ASTCDType> getSymbolDefiningProds(List<? extends ASTCDType> astcdClasses) {
    return astcdClasses.stream()
        .filter(c -> hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }


  /**
   * returns types that get their symbol property from a symbol interface
   * e.g. interface symbol Foo = Name; Bla implements Foo = Name
   * -> than Bla has inherited symbol property
   *
   * @param types
   * @return returns a Map of <the class that inherits the property, the symbol interface full name from which it inherits it>
   */
  public Map<ASTCDType, String> getInheritedSymbolPropertyTypes(List<ASTCDType> types) {
    Map<ASTCDType, String> inheritedSymbolProds = new LinkedHashMap<>();
    for (ASTCDType type : types) {
      // classes with inherited symbol property
      if (hasInheritedSymbolStereotype(type.getModifier())) {
        List<String> stereotypeValues = getStereotypeValues(type.getModifier(), MC2CDStereotypes.INHERITED_SYMBOL);
        // multiple inherited symbols possible
        for (String stereotypeValue : stereotypeValues) {
          inheritedSymbolProds.put(type, stereotypeValue);
        }
      }
    }
    return inheritedSymbolProds;
  }

  /**
   * returns classes that get their symbol property from a symbol interface
   * e.g. interface symbol Foo = Name; Bla implements Foo = Name
   * -> than Bla has inherited symbol property
   *
   * @param astcdClasses
   * @return returns a Map of <the class that inherits the property, the symbol interface full name from which it inherits it>
   */
  public Map<ASTCDClass, String> getInheritedSymbolPropertyClasses(List<ASTCDClass> astcdClasses) {
    Map<ASTCDClass, String> inheritedSymbolProds = new LinkedHashMap<>();
    for (ASTCDClass astcdClass : astcdClasses) {
      // classes with inherited symbol property
      if (hasInheritedSymbolStereotype(astcdClass.getModifier())) {
        List<String> stereotypeValues = getStereotypeValues(astcdClass.getModifier(), MC2CDStereotypes.INHERITED_SYMBOL);
        // multiple inherited symbols possible
        for (String stereotypeValue : stereotypeValues) {
          inheritedSymbolProds.put(astcdClass, stereotypeValue);
        }
      }
    }
    return inheritedSymbolProds;
  }

  public String getInheritedSymbol(ASTCDType astcdClass ) {
    // classes with inherited symbol property
    if (hasInheritedSymbolStereotype(astcdClass.getModifier())) {
      List<String> stereotypeValues = getStereotypeValues(astcdClass.getModifier(), MC2CDStereotypes.INHERITED_SYMBOL);
      if (!stereotypeValues.isEmpty()) {
        return stereotypeValues.get(0);
      }
    }
    return "";
  }

  public List<ASTCDType> getNoSymbolAndScopeDefiningClasses(List<ASTCDClass> astcdClasses) {
    return astcdClasses.stream()
        .filter(c -> !hasSymbolStereotype(c.getModifier()))
        .filter(c -> !hasScopeStereotype(c.getModifier()))
        .filter(c -> !hasInheritedSymbolStereotype(c.getModifier()))
        .filter(c -> !hasInheritedScopeStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  public List<ASTCDType> getOnlyScopeClasses(ASTCDDefinition astcdDefinition) {
    // returns only the implemented classes (no abstract classes and no interfaces)
    List<ASTCDType> symbolProds = astcdDefinition.getCDClassesList().stream()
        .filter(c -> !c.getModifier().isAbstract())
        .filter(c -> (hasScopeStereotype(c.getModifier()) || hasInheritedScopeStereotype(c.getModifier())))
        .filter(c -> !hasSymbolStereotype(c.getModifier()))
        .filter(c -> !hasInheritedSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
    return symbolProds;
  }

  public boolean hasSymbolSpannedScope(ASTCDType symbolProd){
    ASTModifier m = symbolProd.getModifier();
    if(!hasSymbolStereotype(m)){
      return false;
    }
    return hasScopeStereotype(m) || hasInheritedScopeStereotype(m);
  }


  public boolean hasProd(ASTCDDefinition astcdDefinition) {
    // is true if it has any class productions or any interface productions that are not the language interface
    return !astcdDefinition.getCDClassesList().isEmpty() ||
        (!astcdDefinition.getCDInterfacesList().isEmpty() &&
            (astcdDefinition.getCDInterfacesList().size() != 1
                || checkInterfaceNameForProd(astcdDefinition)));
  }

  /**
   * Checks if there is an interface name that does match the language
   * interface. This check is performed against the unqualified and qualified
   * language interface. This check is supposed to be invoked when exactly one
   * interface is present. Otherwise, this method returns true as the CD is
   * assumed to have more interfaces, thus having at least one production.
   *
   * @param astcdDefinition The input cd which is checked for interfaces
   * @return True if the single interface matches the language interface name,
   *         false otherwise
   */
  protected boolean checkInterfaceNameForProd(ASTCDDefinition astcdDefinition) {
    if (astcdDefinition.getCDInterfacesList().size() != 1) {
      return true;
    }
    String interfaceName = astcdDefinition.getCDInterfacesList().get(0).getName();
    // check unqualified interface name
    if (interfaceName.equals(getSimpleLanguageInterfaceName())) {
      return false;
    }

    // check qualified interface name if symbol is available
    if (astcdDefinition.isPresentSymbol()) {
      DiagramSymbol sym = astcdDefinition.getSymbol();
      String qualifiedName = getASTPackage(sym) + "." + AST_PREFIX + sym.getName() + NODE_SUFFIX;
      return !(interfaceName.equals(qualifiedName));
    }

    // per default, we assume that productions are available
    return true;
  }

  public String removeASTPrefix(ASTCDType clazz) {
    // normal symbol name calculation from
    return removeASTPrefix(clazz.getName());
  }

  public String removeASTPrefix(String clazzName) {
    // normal symbol name calculation from
    if (clazzName.startsWith(AST_PREFIX)) {
      return clazzName.substring(AST_PREFIX.length());
    } else {
      return clazzName;
    }
  }

  public boolean hasComponentStereotype(ASTCDDefinition astcdDefinition) {
    return hasComponentStereotype(astcdDefinition.getModifier());
  }

  public boolean hasInheritedSymbolStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.INHERITED_SYMBOL);
  }

  public boolean hasInheritedScopeStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.INHERITED_SCOPE);
  }

  public boolean hasComponentStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.COMPONENT);
  }

  public boolean hasShadowingStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SHADOWING);
  }

  public boolean hasNonExportingStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.NON_EXPORTING);
  }

  public boolean hasOrderedStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.ORDERED);
  }

  public String determineReturnType(ASTMCType type) {
    if (type instanceof ASTMCPrimitiveType) {
      ASTMCPrimitiveType primitiveType = (ASTMCPrimitiveType) type;
      if (primitiveType.isBoolean()) {
        return "false";
      } else {
        return "0";
      }
    } else {
      return "null";
    }
  }
  
  protected List<String> getStereotypeValues(ASTCDAttribute ast, String stereotypeName) {
    List<String> values = Lists.newArrayList();
    if (ast.getModifier().isPresentStereotype()) {
      ast.getModifier().getStereotype().getValuesList().stream()
          .filter(value -> value.getName().equals(stereotypeName))
          .filter(value -> !value.getValue().isEmpty())
          .forEach(value -> values.add(value.getValue()));
    }
    return values;
  }

  public String removeSymbolSuffix(String clazzName) {
    // normal symbol name calculation from
    if (clazzName.endsWith(SYMBOL_SUFFIX)) {
      return clazzName.substring(0, clazzName.length()-SYMBOL_SUFFIX.length());
    } else {
      return clazzName;
    }
  }


}
