/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._visitor.builder.DelegatorVisitorBuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.PACKAGE;
import static de.monticore.codegen.cd2java.CoreTemplates.createPackageHookPoint;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PACKAGE;
import static de.monticore.codegen.mc2cd.TransformationHelper.existsHandwrittenClass;
import static de.monticore.utils.Names.constructQualifiedName;

public class CDTraverserDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  protected final TraverserInterfaceDecorator traverserDecorator;

  protected final IterablePath handCodedPath;

  protected final VisitorService visitorService;

  public CDTraverserDecorator(final GlobalExtensionManagement glex,
                            final IterablePath handCodedPath,
                            final VisitorService visitorService,
                            final TraverserInterfaceDecorator traverserDecorator) {
    super(glex);
    this.handCodedPath = handCodedPath;
    this.visitorService = visitorService;
    this.traverserDecorator = traverserDecorator;
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit input) {
    List<String> visitorPackage = Lists.newArrayList();
    input.getPackageList().forEach(p -> visitorPackage.add(p.toLowerCase()));
    visitorPackage.addAll(Arrays.asList(input.getCDDefinition().getName().toLowerCase(), VISITOR_PACKAGE));

    setIfExistsHandwrittenFile(visitorPackage);

    ASTCDInterface traverserInterface = traverserDecorator.decorate(input);

    // add decorators here and collect classes / interfaces
    
    
    
    // build cd
    ASTCDDefinition astCD = CD4CodeMill.cDDefinitionBuilder()
        .setName(input.getCDDefinition().getName())
        .addCDInterface(traverserInterface)
        .build();

    for (ASTCDClass cdClass : astCD.getCDClassList()) {
      this.replaceTemplate(PACKAGE, cdClass, createPackageHookPoint(visitorPackage));
    }

    for (ASTCDInterface cdInterface : astCD.getCDInterfaceList()) {
      this.replaceTemplate(CoreTemplates.PACKAGE, cdInterface, createPackageHookPoint(visitorPackage));
    }

    return CD4CodeMill.cDCompilationUnitBuilder()
        .setPackageList(visitorPackage)
        .setCDDefinition(astCD)
        .build();
  }

  protected void setIfExistsHandwrittenFile(List<String> visitorPackage) {
    boolean isVisitorHandCoded = existsHandwrittenClass(handCodedPath,
        constructQualifiedName(visitorPackage, visitorService.getVisitorSimpleName()));
    traverserDecorator.setTop(isVisitorHandCoded);
  }
}
