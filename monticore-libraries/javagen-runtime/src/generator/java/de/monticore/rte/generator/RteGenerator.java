package de.monticore.rte.generator;

import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodBuilder;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnTypeBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._ast.ASTTypeParameters;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public abstract class RteGenerator {

  protected final GeneratorSetup s;

  public RteGenerator(File outputDirectory) {
    this.s = new GeneratorSetup();
    s.getGlex().setGlobalValue("cdPrinter", new CdUtilsPrinter());
    s.setOutputDirectory(outputDirectory);
  }

  protected void setBody(ASTNode node, String template, Object... args) {
    s.getGlex().replaceTemplate(EMPTY_BODY, node, new TemplateHookPoint(template, args));
  }

  protected void setAnnotations(ASTNode node, String text) {
    s.getGlex().replaceTemplate(ANNOTATIONS, node, new StringHookPoint(text));
  }

  protected void setOverride(ASTNode node) {
    setAnnotations(node, "@Override");
  }

  protected abstract String getClassName(int n);

  protected abstract ASTCDConstructor constructConstructor(ASTCDConstructor constructor, int n);

  protected abstract List<ASTCDMethod> constructMethods(int n);

  protected List<ASTCDAttribute> constructAttributes(int n) {
    return List.of();
  }

  protected List<ASTTypeParameter> constructTypeParameterList(int n) {
    return List.of();
  }

  protected ASTTypeParameters constructTypeParameters(int n) {
    return buildTypeParameters(constructTypeParameterList(n).toArray(ASTTypeParameter[]::new));
  }

  protected ASTMCType buildType(String typeName) {
    return TransformationHelper.createType(typeName);
  }

  protected ASTCDMethodBuilder buildMethod(String name, ASTModifier modifier, String typeName, ASTCDParameter... parameters) {
    ASTMCReturnTypeBuilder returnTypeBuilder = CD4CodeMill.mCReturnTypeBuilder();
    if (typeName != null) {
      returnTypeBuilder.setMCType(buildType(typeName));
    }
    else {
      returnTypeBuilder.setMCVoidType(CD4CodeMill.mCVoidTypeBuilder().build());
    }
    return CD4CodeMill.cDMethodBuilder().setName(name).setModifier(modifier).setCDParametersList(List.of(parameters)).setMCReturnType(returnTypeBuilder.build());
  }

  protected ASTTypeParameters buildTypeParameters(ASTTypeParameter... typeParameters) {
    return CD4CodeMill.typeParametersBuilder().addAllTypeParameters(List.of(typeParameters)).build();
  }

  protected ASTCDMethod completeMethod(ASTCDMethodBuilder builder, String template, Object... args) {
    ASTCDMethod method = builder.build();
    setBody(method, template, args);
    return method;
  }

  protected ASTCDParameter buildParameter(String name, String typeName) {
    return CD4CodeMill.cDParameterBuilder().setName(name).setMCType(buildType(typeName)).build();
  }

  protected ASTCDAttribute buildAttribute(String name, ASTModifier modifier, String typeName) {
    return CD4AnalysisMill.cDAttributeBuilder().setName(name).setModifier(modifier).setMCType(buildType(typeName)).build();
  }

  protected int getMinSize() {
    return 0; // Minimum size for tuples
  }

  public void buildClasses(int maxSize) {
    for (int i = getMinSize(); i <= maxSize; i++) {
      buildClass(i);
    }
  }

  protected List<ASTCDMember> constructMembers(int n) {
    List<ASTCDMember> members = new ArrayList<>();
    members.addAll(constructMethods(n));
    members.addAll(constructAttributes(n));
    ASTCDConstructor constructor = constructConstructor(CDConstructorFacade.getInstance().createConstructor(CDModifier.PROTECTED.build(), getClassName(n)), n);
    if (constructor != null) {
      members.add(constructor);
    }
    return members;
  }

  public ASTCDElement constructClass(int n) {
    return CD4CodeMill.cDClassBuilder().setModifier(CDModifier.PUBLIC.build()).setTypeParameters(constructTypeParameters(n)).setName(getClassName(n)).addAllCDMembers(constructMembers(n)).build();
  }

  public void buildClass(int n) {
    if (n < getMinSize()) {
      throw new IllegalArgumentException("Tuple size must be at least 1");
    }

    ASTCDElement clazz = constructClass(n);

    ASTMCQualifiedName pckName = CD4CodeMill.mCQualifiedNameBuilder().setPartsList(buildPackageName()).build();

    ASTCDDefinition def = CD4AnalysisMill.cDDefinitionBuilder().setName("definition").setModifier(CDModifier.PUBLIC.build()).addCDElement(CD4CodeMill.cDPackageBuilder().setMCQualifiedName(pckName).addCDElement(clazz).build()).build();

    ASTMCPackageDeclaration pkgD = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(pckName).build();

    // AST Root
    ASTCDCompilationUnit u = CD4AnalysisMill.cDCompilationUnitBuilder().setMCPackageDeclaration(pkgD).setCDDefinition(def).build();

    CDGenerator g = new CDGenerator(s);
    g.generate(u);
  }

  protected abstract List<String> buildPackageName();

}
