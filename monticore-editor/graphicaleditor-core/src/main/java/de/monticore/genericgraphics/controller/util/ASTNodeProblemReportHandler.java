/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditPart;

import de.monticore.ast.ASTNode;
import de.monticore.genericgraphics.controller.editparts.IMCGraphicalEditPart;
import de.monticore.genericgraphics.controller.persistence.ErrorCollector;
import de.monticore.genericgraphics.controller.selection.IHasLineNumbers;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;


/**
 * <p>
 * A utility class that provides static methods for showing
 * {@link Finding ProblemReports} in
 * <ul>
 * <li>a Graphical Editor:
 * {@link #showProblemReportsInGraphics(Collection, ErrorCollector)}</li>
 * <li>the eclipse problem view:
 * {@link #showProblemReportsInProblemsView(IFile, ErrorCollector)}</li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public class ASTNodeProblemReportHandler {
  /**
   * <p>
   * Shows {@link ProblemReport ProblemReports} in the Graphic Representation.
   * </p>
   * This method creates a mapping from linenumbers of ASTNodes to EditParts.
   * Then it iterates through all problems and uses the {@line
   * IProblemReportHandler} interface to display errors.
   * 
   * @param editparts The {@link EditPart EditParts} that should be marked with
   *          problems.
   * @param ec The {@link ErrorCollector} used as input for the
   *          {@link ProblemReport ProblemReports}
   */
  public static void showProblemReportsInGraphics(Collection<EditPart> editparts) {
    // create mapping linenumber to editparts
    Map<Integer, List<IMCGraphicalEditPart>> linesToEP = new LinkedHashMap<Integer, List<IMCGraphicalEditPart>>();
    
    // create mapping of EP to number of lines in source code
    Map<IMCGraphicalEditPart, Integer> epToLineCount = new LinkedHashMap<IMCGraphicalEditPart, Integer>();
    
    for (Object o : editparts) {
      // only graphical editparts are interesting
      if (!(o instanceof IMCGraphicalEditPart)) {
        continue;
      }
      
      int startLine = Integer.MIN_VALUE;
      int endLine = Integer.MIN_VALUE;
      
      IMCGraphicalEditPart ep = (IMCGraphicalEditPart) o;
      Object model = ep.getModel();
      if (model instanceof ASTNode) {
        ASTNode node = (ASTNode) model;
        SourcePosition start = node.get_SourcePositionStart();
        SourcePosition end = node.get_SourcePositionEnd();
        
        if (start != null && end != null) {
          startLine = start.getLine();
          endLine = end.getLine();
        }
      }
      else if (model instanceof IHasLineNumbers) {
        IHasLineNumbers n = (IHasLineNumbers) model;
        startLine = n.getStartLine();
        endLine = n.getEndLine();
      }
      
      if (startLine != Integer.MIN_VALUE && endLine != Integer.MIN_VALUE) {
        epToLineCount.put(ep, endLine - startLine + 1);
        for (int line = startLine; line <= endLine; line++) {
          List<IMCGraphicalEditPart> list = linesToEP.get(line);
          if (list == null) {
            list = new ArrayList<IMCGraphicalEditPart>();
            linesToEP.put(line, list);
          }
          list.add(ep);
        }
      }
    }
    
    // create map from line number to list of ProblemReports
    // this is necessary, because there could be several errors in one line
    Map<Integer, List<Finding>> linesToReports = new HashMap<Integer, List<Finding>>();
    
    for (Finding report : Log.getFindings()) {
      int line = SourcePosition.getDefaultSourcePosition().getLine() + 1;
      if (report.getSourcePosition().isPresent()) {
        line = report.getSourcePosition().get().getLine();
      }
      List<Finding> probs = linesToReports.get(line);
      if (probs == null) {
        probs = new ArrayList<Finding>();
      }
      probs.add(report);
      linesToReports.put(line, probs);
    }
    
    for (Entry<Integer, List<Finding>> entry : linesToReports.entrySet()) {
      // set the errors
      List<IMCGraphicalEditPart> eps = linesToEP.get(entry.getKey());
      
      if (eps != null) {
        // there are ASTNodes that have several lines in the source
        // like classes, or states
        // if a method of a class is selected, the corresponding
        // class should not also be selected.
        // therefore, only the ones with smallest number of lines are selected.
        
        // find EP(s) with smallest number of lines
        int smallestLineCount = Integer.MAX_VALUE;
        for (IMCGraphicalEditPart ep : eps) {
          smallestLineCount = Math.min(epToLineCount.get(ep), smallestLineCount);
        }
        
        // set problems
        for (IMCGraphicalEditPart ep : eps) {
          // set problem only if EP number of lines is not bigger
          // as the smallest one
          if (epToLineCount.get(ep) <= smallestLineCount) {
            ep.setProblems(entry.getValue());
          }
        }
      }
    }
  }
  
  /**
   * Shows {@link Finding Findings} in ProblemsView of eclipse. All
   * existing reports will be deleted before.
   * 
   * @param file The {@link IFile} where the problem markers refer to.
   * @param ec The {@link ErrorCollector} used as input for the
   *          {@link Finding Findings}
   */
  public static void showFindingsInProblemsView(final IFile file, ErrorCollector ec) {
    
    List<Finding> reports = ec.getReports();
    
    // delete old markers
    try {
      file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    }
    catch (CoreException e) {
      Log.error("0xA1110 ASTNodeProblemReportHandler: Deleting markers failed: " + e);
    }
    
    // set new markers
    if (!reports.isEmpty()) {
      
      // create mapping
      final HashMap<String, Object> theMap = new HashMap<String, Object>();
      for (Finding report : reports) {
        theMap.clear();
        theMap.put(IMarker.MESSAGE, report.getMsg());
        theMap.put(IMarker.LINE_NUMBER, report.getSourcePosition().orElse(SourcePosition.getDefaultSourcePosition()).getLine());
        
        if (report.getType() == Finding.Type.ERROR) {
          theMap.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
        }
        else if (report.getType() == Finding.Type.WARNING) {
          theMap.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        }
        else {
          assert false : "unknown type!";
        }
      }
      
      IWorkspaceRunnable r = new IWorkspaceRunnable() {
        @Override
        public void run(IProgressMonitor monitor) throws CoreException {
          IMarker marker = file.createMarker(IMarker.PROBLEM);
          marker.setAttributes(theMap);
        }
      };
      
      try {
        file.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, null);
      }
      catch (CoreException e) {
        e.printStackTrace();
      }
      
    }
    // getDocumentProvider().changed(getDocumentProvider().getDocument(null));
  }
}
