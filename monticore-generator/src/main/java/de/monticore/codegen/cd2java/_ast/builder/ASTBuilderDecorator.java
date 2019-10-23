/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.ast.ASTCNode;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.*;

/**
 * uses the BuilderDecorator and adds ASTBuilder specific properties
 */
public class ASTBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final BuilderDecorator builderDecorator;

  public ASTBuilderDecorator(final GlobalExtensionManagement glex, final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass domainClass) {
    ASTCDClass builderClass = this.builderDecorator.decorate(domainClass);
    String builderClassName = builderClass.getName();

    builderClass.setSuperclass(createBuilderSuperClass(domainClass, builderClassName));

    if (!hasSuperClassOtherThanASTCNode(domainClass)) {
      ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(builderClassName);
      builderClass.addAllCDMethods(createBuilderMethodForASTCNodeMethods(builderType));
    }

    Optional<ASTCDMethod> buildMethod = builderClass.getCDMethodList().stream().filter(m -> BUILD_METHOD.equals(m.getName())).findFirst();
    buildMethod.ifPresent(b ->
        this.replaceTemplate(BUILD_INIT_TEMPLATE, b, new TemplateHookPoint(AST_BUILDER_INIT_TEMPLATE, domainClass)));

    return builderClass;
  }


  protected ASTMCQualifiedType createBuilderSuperClass(final ASTCDClass domainClass, final String builderClassName) {
    String superClass = String.format(DEFAULT_SUPER_CLASS, builderClassName);
    if (hasSuperClassOtherThanASTCNode(domainClass)) {
      superClass = domainClass.printSuperClass()+ BUILDER_SUFFIX;
    }
    return this.getMCTypeFacade().createQualifiedType(superClass);
  }

  /**
   * checks what superclass is needed
   * corresponding AST has own superClass -> use that superBuilder
   * corresponding AST has just ASTCNode as superclass -> use ASTCNodeBuilder as superclass
   */
  protected boolean hasSuperClassOtherThanASTCNode(final ASTCDClass domainClass) {
    return domainClass.isPresentSuperclass() && !ASTCNode.class.getSimpleName().equals(domainClass.printSuperClass());
  }

  protected List<ASTCDMethod> createBuilderMethodForASTCNodeMethods(final ASTMCType builderType) {
    List<ASTCDMethod> result = new ArrayList<>();
    for (ASTCNodeMethod astNodeMethod : ASTCNodeMethod.values()) {
      ASTCDMethod method = this.getCDMethodFacade().createMethodByDefinition(astNodeMethod.signature);
      ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(builderType).build();
      method.setMCReturnType(returnType);
      this.replaceTemplate(EMPTY_BODY, method, createImplementation(method));
      result.add(method);
    }
    return result;
  }

  protected HookPoint createImplementation(final ASTCDMethod method) {
    String methodName = method.getName();
    String parameterCall = method.getCDParameterList().stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));
    return new TemplateHookPoint("_ast.ast_class.builder.ASTCNodeMethodDelegate", methodName, parameterCall);
  }

  protected enum ASTCNodeMethod {
    // ----------- SourcePosition -----------------------------
    set_SourcePositionEnd("public void set_SourcePositionEnd(SourcePosition end);"),
    set_SourcePositionEndOpt("public void set_SourcePositionEndOpt(Optional<SourcePosition> end);"),
    set_SourcePositionEndAbsent("public void set_SourcePositionEndAbsent();"),
    set_SourcePositionStart("public void set_SourcePositionStart(SourcePosition start);"),
    set_SourcePositionStartOpt("public void set_SourcePositionStartOpt(Optional<SourcePosition> Start);"),
    set_SourcePositionStartAbsent("public void set_SourcePositionStartAbsent();"),
    // ----------- PreComments -----------------------------
    clear_PreComments("public void clear_PreComments();"),
    add_PreComment("public void add_PreComment(Comment precomment);"),
    add_PreComment_("public void add_PreComment(int index, Comment precomment);"),
    addAll_PreComments("public boolean addAll_PreComments(Collection<Comment> precomments);"),
    addAll_PreComments_("public boolean addAll_PreComments(int index, Collection<Comment> precomments);"),
    remove_PreComment("public boolean remove_PreComment(Object element);"),
    remove_PreComment_("public boolean remove_PreComment(int index);"),
    removeAll_PreComments("public boolean removeAll_PreComments(Collection<?> element);"),
    retainAll_PreComments("public boolean retainAll_PreComments(Collection<?> element);"),
    removeIf_PreComment("public boolean removeIf_PreComment(Predicate<? super Comment> filter);"),
    forEach_PreComments("public void forEach_PreComments(Consumer<? super Comment> action);"),
    replaceAll_PreComments("public void replaceAll_PreComments(UnaryOperator<Comment> operator);"),
    sort_PreComments("public void sort_PreComments(Comparator<? super Comment> comparator);"),
    set_PreCommentList("public void set_PreCommentList(List<Comment> preComments);"),
    set_PreComment("public Comment set_PreComment(int index, Comment precomment);"),
    // ----------- PostComments -----------------------------
    clear_PostComments("public void clear_PostComments();"),
    add_PostComment("public void add_PostComment(Comment postcomment);"),
    add_PostComment_("public void add_PostComment(int index, Comment postcomment);"),
    addAll_PostComments("public boolean addAll_PostComments(Collection<Comment> postcomments);"),
    addAll_PostComments_("public boolean addAll_PostComments(int index, Collection<Comment> postcomments);"),
    remove_PostComment("public boolean remove_PostComment(Object element);"),
    remove_PostComment_("public boolean remove_PostComment(int index);"),
    removeAll_PostComments("public boolean removeAll_PostComments(Collection<?> element);"),
    retainAll_PostComments("public boolean retainAll_PostComments(Collection<?> element);"),
    removeIf_PostComment("public boolean removeIf_PostComment(Predicate<? super Comment> filter);"),
    forEach_PostComments("public void forEach_PostComments(Consumer<? super Comment> action);"),
    replaceAll_PostComments("public void replaceAll_PostComments(UnaryOperator<Comment> operator);"),
    sort_PostComments("public void sort_PostComments(Comparator<? super Comment> comparator);"),
    set_PostCommentList("public void set_PostCommentList(List<Comment> postComments);"),
    set_PostComment("public Comment set_PostComment(int index, Comment postcomment);");

    private final String signature;

    ASTCNodeMethod(String signature) {
      this.signature = signature;
    }
  }
}
