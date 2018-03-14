/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.transl.creation.CDASTCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * The term translation applies to transformations that transfer information from one AST to
 * another.
 * This translation is a composite of multiple atomic translations. It takes in a single root Link,
 * builds up the entire CD AST from there and then transfers various pieces of information from the
 * MC AST to the CD AST.
 *
 * @author Sebastian Oberhoff
 */
public class MC2CDTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  private GlobalExtensionManagement glex;
  
  private LexNamer lexNamer;

  public MC2CDTranslation(GlobalExtensionManagement glex) {
    this.glex = glex;
    this.lexNamer = (glex.hasGlobalVar("lexNamer")
        && glex.getGlobalVar("lexNamer") instanceof LexNamer)
            ? (LexNamer) glex.getGlobalVar("lexNamer")
            : new LexNamer();
  }

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    return new CDASTCreator()
        .andThen(new InheritedAttributesTranslation())
        .andThen(new PackageTranslation())
        .andThen(new StarImportSuperGrammarTranslation())
        .andThen(new NameTranslation())
        .andThen(new ExtendsTranslation())
        .andThen(new ImplementsTranslation())
        .andThen(new ASTRuleInheritanceTranslation())
        .andThen(new MethodTranslation(glex))
        .andThen(new OverridingClassProdTranslation())
        .andThen(new RemoveOverriddenAttributesTranslation())
        .andThen(new AbstractProdModifierTranslation())
        .andThen(new ReferenceTypeTranslation())
        .andThen(new EnumTranslation())
        .andThen(new ExternalImplementationTranslation())
        .andThen(new ConstantTypeTranslation())
        .andThen(new CreateConstantAttributeTranslation())
        .andThen(new MultiplicityTranslation())
        .andThen(new ConstantsTranslation(lexNamer))
        .andThen(new NonTerminalsWithSymbolReferenceToCDAttributeStereotypes())
        .apply(rootLink);
  }
}
