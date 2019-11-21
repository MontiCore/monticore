/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.se_rwth.commons.Names;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SymbolTableReporter2 extends AReporter {
  
  static final String SIMPLE_FILE_NAME = "13_SymbolTable";
  
  protected final String outputDir;
  
  protected final String modelName;
  
  protected final ReportingRepository repository;
  
  protected boolean printEmptyOptional = false;
  
  protected boolean printAllFieldsCommented = false;
  
  protected boolean printEmptyList = false;
  
  public SymbolTableReporter2(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator
        + modelName,
        Names.getSimpleName(modelName) + "_ST", ReportingConstants.OD_FILE_EXTENSION);
    this.outputDir = outputDir;
    this.modelName = modelName;
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("//========================================================== Symbol Table");
  }
  
  private void writeFooter() {
    writeLine("//========================================================== Explanation");
    writeLine("//Shows symbol table after finishing the generation process.");
  }
  
  @Override
  public void flush(ASTNode ast) {
    if (ast != null) {
      // TODO for new symtab
    }
    writeFooter();
    super.flush(ast);
  }
  
  protected void reportScope(IScope scope, IndentPrinter printer) {
    // final Collection<ISymbol> symbols = Scopes.getLocalSymbolsAsCollection(scope);
    final Collection<ISymbol> symbols = new ArrayList<>(); // TODO: new symtab has separate lists of
                                                           // symbols by kind
    String type;
    String scopeName;
    scopeName = repository.getScopeNameFormatted(scope);
    type = Names.getSimpleName(scope.getClass().getName());
    printer.println(scopeName + ": " + type + "{");
    printer.indent();
    reportAllFields(scope.getClass(), printer);
    
    if (scope.isPresentName()) {
      printer.println("name = \"" + scope.getName() + "\";");
    }
    else if (printEmptyOptional) {
      printer.println("name = absent;");
    }
    
    // TODO: ArtifactScopes do not have a common super class in new symtab
    // if (scope.getClass().getName().endsWith("ArtifactScope")) {
    // ArtifactScope artifactScope = (ArtifactScope) scope;
    // if (!artifactScope.getImports().isEmpty()) {
    // printer.print("imports = [\"");
    // String sep = "";
    // for (ImportStatement imp: artifactScope.getImports()) {
    // printer.print(sep);
    // sep = ", ";
    // printer.print(imp.getStatement());
    // printer.print("\"");
    // }
    // printer.println("];");
    // } else if (printEmptyList) {
    // printer.println("imports = [];");
    // }
    //
    // printer.println("packageName = \"" + artifactScope.getPackageName() + "\";");
    // }
    
    printer.println("isShadowingScope = " + scope.isShadowing() + ";");
    printer.println("exportsSymbols = " + scope.isExportingSymbols() + ";");
    
    if (scope.isPresentAstNode()) {
      printer.print("astNode = ");
      printer.print(repository.getASTNodeNameFormatted(scope.getAstNode()));
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.println("astNode = absent;");
    }
    
    if (scope.isPresentSpanningSymbol()) {
      printer.print("spanningSymbol = ");
      reportSymbol(scope.getSpanningSymbol(), printer);
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.println("spanningSymbol = absent;");
    }

    if (scope.getEnclosingScope() != null) {
      printer.print("enclosingScope = ");
      printer.print(repository.getScopeNameFormatted(scope.getEnclosingScope()));
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.println("enclosingScope = absent;");
    }
    
    Collection<ISymbol> reportedSymbols = symbols.stream()
        .filter(sym -> !(sym instanceof IScopeSpanningSymbol)).collect(Collectors.toList());
    if (!reportedSymbols.isEmpty()) {
      printer.print("symbols = [");
      printer.println("// *size: " + reportedSymbols.size());
      printer.indent();
    }
    else if (printEmptyList) {
      printer.println("symbols = [];");
    }
    String sep = "";
    for (ISymbol symbol : reportedSymbols) {
      if (!(symbol instanceof IScopeSpanningSymbol)) {
        printer.print(sep);
        sep = ",\n";
        reportSymbol(symbol, printer);
      }
    }
    if (!reportedSymbols.isEmpty()) {
      printer.println("];");
      printer.unindent();
    }
    
    // TODO: Supscopes have different access methods in new symtab
    // if (!scope.getSubScopes().isEmpty()) {
    // printer.print("subScopes = [");
    // printer.println("// *size: " + scope.getSubScopes().size());
    // printer.indent();
    // }
    // else if (printEmptyList) {
    // printer.println("subScopes = [];");
    // }
    // sep = "";
    // for (IScope subScope : scope.getSubScopes()) {
    // printer.print(sep);
    // sep = ",\n";
    // reportScope(subScope, printer);
    // }
    // if (!scope.getSubScopes().isEmpty()) {
    // printer.println("];");
    // printer.unindent();
    // }
    
    printer.unindent();
    printer.print("}");
  }
  
  @Override
  public void reportSymbolTableScope(IScope scope) {
    IndentPrinter printer = new IndentPrinter();
    printer.println("objectdiagram " + Names.getSimpleName(modelName) + "_ST {");
    printer.indent();
    reportScope(scope, printer);
    printer.println(";");
    printer.println();
    printer.unindent();
    printer.println("}");
    writeLine(printer.getContent());
  }
  
  protected void reportSymbol(ISymbol sym, IndentPrinter printer) {
    String type = Names.getSimpleName(sym.getClass().getSimpleName());
    String symName = repository.getSymbolNameFormatted(sym);
    printer.println(symName + ": " + type + " {");
    printer.indent();
    reportAttributes(sym, printer);
    printer.unindent();
    printer.print("}");
  }
  
  protected void reportAccessModifier(AccessModifier.AllInclusionAccessModifier modifier,
      IndentPrinter printer) {
    String type = modifier.getClass().getSimpleName();
    printer.println("accessModifier = :" + type + "{};");
  }
  
  protected void reportAccessModifier(BasicAccessModifier modifier, IndentPrinter printer) {
    printer.println("accessModifier = \"BasicAccessModifier." + modifier.toString() + "\";");
  }
  
  protected void reportAccessModifier(AccessModifier modifier, IndentPrinter printer) {
    if (modifier instanceof BasicAccessModifier) {
      reportAccessModifier((BasicAccessModifier) modifier, printer);
    }
    else if (modifier instanceof AccessModifier.AllInclusionAccessModifier) {
      reportAccessModifier((AccessModifier.AllInclusionAccessModifier) modifier, printer);
    }
  }
  
  protected void reportAttributes(ISymbol sym, IndentPrinter printer) {
    reportAllFields(sym.getClass(), printer);
    printer.println("name = \"" + sym.getName() + "\";");
    printer.println("kind = \"" + sym.getClass().getName() + "\";");
    if (sym.isPresentAstNode()) {
      printer.print("astNode = ");
      printer.print(repository.getASTNodeNameFormatted(sym.getAstNode()));
      printer.println(";");
    }
    else if (printEmptyOptional) {
      printer.print("astNode = absent;");
    }
    if (sym instanceof IScopeSpanningSymbol) {
      IScopeSpanningSymbol spanningSym = (IScopeSpanningSymbol) sym;
      printer.println("spannedScope = "
          + repository.getScopeNameFormatted(spanningSym.getSpannedScope()) + ";");
    }
    
    printer.print("enclosingScope = ");
    // printer.print(repository.getScopeNameFormatted(sym.getEnclosingScope()));
    // TODO: new symtab doesn't have enclosing scope in symbol interface
    printer.println(";");
    
    reportAccessModifier(sym.getAccessModifier(), printer);
  }
  
//   protected void reportCommonJFieldAttributes(
//   CommonJFieldSymbol<? extends JTypeReference<? extends JTypeSymbol>> sym,
//   IndentPrinter printer) {
//   printer.println("isFinal = " + sym.isFinal() + ";");
//   printer.println("isParameter = " + sym.isParameter() + ";");
//   printer.println("isStatic = " + sym.isStatic() + ";");
//   }
  //
  // protected void reportCommonJMethodAttributes(
  // CommonJMethodSymbol<? extends JTypeSymbol, ? extends JTypeReference<? extends JTypeSymbol>, ?
  // extends JFieldSymbol> sym,
  // IndentPrinter printer) {
  // printer.println("isFinal = " + sym.isFinal() + ";");
  // printer.println("isStatic = " + sym.isStatic() + ";");
  // printer.println("isAbstract = " + sym.isAbstract() + ";");
  // printer.println("isConstructor = " + sym.isConstructor() + ";");
  // printer.println("isEllipsisParameterMethod = " + sym.isEllipsisParameterMethod() + ";");
  // if (!sym.isConstructor()) {
  // printer.println("returnType = "
  // + repository.getSymbolNameFormatted(sym.getReturnType()) + ";");
  // }
  // reportListOfReferences("exceptions", sym.getExceptions(), printer);
  // }
  //
  // protected void reportCommonJTypeAttributes(
  // CommonJTypeSymbol<? extends JTypeSymbol, ? extends JFieldSymbol, ? extends JMethodSymbol, ?
  // extends JTypeReference<? extends JTypeSymbol>> sym,
  // IndentPrinter printer) {
  // printer.println("isFinal = " + sym.isFinal() + ";");
  // printer.println("isAbstract = " + sym.isAbstract() + ";");
  // printer.println("isEnum = " + sym.isEnum() + ";");
  // printer.println("isFormalTypeParameter = " + sym.isFormalTypeParameter() + ";");
  // printer.println("isInnerType = " + sym.isInnerType() + ";");
  // printer.println("isInterface = " + sym.isInterface() + ";");
  // if (sym.getSuperClass().isPresent()) {
  // printer.println("superClass = "
  // + repository.getSymbolNameFormatted(sym.getSuperClass().get())
  // + ";");
  // }
  // else if (printEmptyOptional) {
  // printer.print("superClass = absent;");
  // }
  // reportListOfReferences("interfaces", sym.getInterfaces(), printer);
  // }
  
//  protected void reportListOfReferences(String listName,
//      Collection<? extends JTypeReference<? extends JTypeSymbol>> refs, IndentPrinter printer) {
//    if (!refs.isEmpty()) {
//      printer.print(listName + " = ");
//      String delim = "";
//      for (JTypeReference<? extends JTypeSymbol> anno : refs) {
//        printer.print(delim);
//        printer.print(repository.getSymbolNameFormatted(anno));
//        delim = ", ";
//      }
//      printer.println(";");
//    }
//    else if (printEmptyList) {
//      printer.println(listName + " = [];");
//    }
//  }
//  
  /**
   * @return the printEmptyOptional
   */
  public boolean isPrintEmptyOptional() {
    return this.printEmptyOptional;
  }
  
  /**
   * @param printEmptyOptional the printEmptyOptional to set
   */
  public void setPrintEmptyOptional(boolean printEmptyOptional) {
    this.printEmptyOptional = printEmptyOptional;
  }
  
  /**
   * @return the printEmptyList
   */
  public boolean isPrintEmptyList() {
    return this.printEmptyList;
  }
  
  /**
   * @return the printAllFieldsCommented
   */
  public boolean isPrintAllFieldsCommented() {
    return this.printAllFieldsCommented;
  }
  
  /**
   * @param printAllFieldsCommented the printAllFieldsCommented to set
   */
  public void setPrintAllFieldsCommented(boolean printAllFieldsCommented) {
    this.printAllFieldsCommented = printAllFieldsCommented;
  }
  
  /**
   * @param printEmptyList the printEmptyList to set
   */
  public void setPrintEmptyList(boolean printEmptyList) {
    this.printEmptyList = printEmptyList;
  }
  
  protected void reportAllFields(Class<?> clazz, IndentPrinter printer) {
    if (!isPrintAllFieldsCommented()) {
      return;
    }
    printer.print("/* fields = ");
    for (Field field : clazz.getDeclaredFields()) {
      printer.print(field.getName() + " ");
    }
    while (clazz.getSuperclass() != null) {
      clazz = clazz.getSuperclass();
      for (Field field : clazz.getDeclaredFields()) {
        printer.print(field.getName() + " ");
      }
    }
    printer.println("*/");
  }
  
}
