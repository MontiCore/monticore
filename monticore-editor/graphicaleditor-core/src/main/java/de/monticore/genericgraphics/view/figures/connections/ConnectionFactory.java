/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures.connections;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

/**
 * <p>
 * Factory for creation of Connections.
 * </p>
 * <p>
 * This factory creates:
 * <ul>
 * <li><b>Connections</b> that handle collisions of more than one connection
 * with the same target and source anchors positions (by use of a
 * {@link FanRouter})</li>
 * <li><b>Self-Referencing Connections</b>: that handle collisions of more than
 * one connection with the same target and source anchors positions. (by use of
 * a {@link ManhattanConnectionRouter})</li>
 * </p>
 * 
 * @author Tim Enger
 */
public class ConnectionFactory {
  
  private ConnectionRouter manhattanRouter;
  private FanRouter fanRouter;
  
  protected static ConnectionFactory factory = null;
  
  protected ConnectionFactory() {
    // initialize this routers, since they have to be the same for all
    // connections. This is important for handling collisions between
    // connections, like overlapping.
    fanRouter = new FanRouter();
    manhattanRouter = new ManhattanConnectionRouter();
  }
  
  private static void check() {
    if (factory == null) {
      factory = new ConnectionFactory();
    }
  }
  
  /**
   * <p>
   * Factory method.
   * </p>
   * <p>
   * A self referencing connection, is a connection, that has the same source
   * and target object.<br>
   * <br>
   * I might be the case, that two self referencing connections are placed at
   * the same source and target anchors.<br>
   * To avoid complete overlapping, a {@link ManhattanConnectionRouter} is used,
   * to handle the collision.
   * </p>
   * 
   * @return {@link IFigureEndPolylineConnection}
   */
  public static IFigureEndPolylineConnection createSelfRefIFigureEndConnection() {
    check();
    return factory.doCreateSelfRefIFigureEndConnection();
  }
  
  /**
   * Inner Factory method.
   * 
   * @return {@link IFigureEndPolylineConnection}
   */
  protected IFigureEndPolylineConnection doCreateSelfRefIFigureEndConnection() {
    FigureEndPolylineConnection con = new FigureEndPolylineConnection();
    con.setConnectionRouter(manhattanRouter);
    return con;
  }
  
  /**
   * <p>
   * Convenience Factory method.
   * </p>
   * <p>
   * Calls {@link #createIFigureEndConnection(int)} with the argument
   * {@link SWT#LINE_SOLID}.
   * </p>
   * 
   * @return {@link IFigureEndPolylineConnection}
   */
  public static IFigureEndPolylineConnection createIFigureEndConnection() {
    return createIFigureEndConnection(SWT.LINE_SOLID);
  }
  
  /**
   * <p>
   * Factory method.
   * </p>
   * <p>
   * Create a connection figure that is able to handle bendpoints, by using a
   * {@link BendpointConnectionRouter}.<br>
   * In order to handle collisions of two overlapping connections, a
   * {@link FanRouter} is used as first router, before the
   * {@link BendpointConnectionRouter} is used.
   * </p>
   * 
   * @param lineStyle The style of the connection. Use
   *          <ul>
   *          <li>{@link SWT#LINE_CUSTOM}</li>
   *          <li>{@link SWT#LINE_DASH}</li>
   *          <li>{@link SWT#LINE_DASHDOT}</li>
   *          <li>{@link SWT#LINE_DASHDOTDOT}</li>
   *          <li>{@link SWT#LINE_DOT}</li>
   *          <li>{@link SWT#LINE_SOLID} (default)</li>
   *          </ul>
   * @return {@link IFigureEndPolylineConnection} for Connections
   */
  public static IFigureEndPolylineConnection createIFigureEndConnection(int lineStyle) {
    check();
    return factory.doCreateIFigureEndConnection(lineStyle);
  }
  
  /**
   * Inner Factory method.
   * 
   * @return {@link IFigureEndPolylineConnection}
   */
  protected IFigureEndPolylineConnection doCreateIFigureEndConnection(int lineStyle) {
    FigureEndPolylineConnection con = new FigureEndPolylineConnection();
    fanRouter.setNextRouter(new BendpointConnectionRouter());
    con.setConnectionRouter(fanRouter);
    con.setLineStyle(lineStyle);
    return con;
  }
  
  /**
   * <p>
   * Factory method.
   * </p>
   * <p>
   * A self referencing connection, is a connection, that has the same source
   * and target object.<br>
   * <br>
   * I might be the case, that two self referencing connections are placed at
   * the same source and target anchors.<br>
   * To avoid complete overlapping, a {@link ManhattanConnectionRouter} is used,
   * to handle the collision.
   * </p>
   * 
   * @return {@link PolylineConnection}
   */
  public static PolylineConnection createSelfRefConnection() {
    check();
    return factory.doCreateSelfRefConnection();
  }
  
  /**
   * Inner Factory method.
   * 
   * @return {@link PolylineConnection}
   */
  protected PolylineConnection doCreateSelfRefConnection() {
    PolylineConnection con = new PolylineConnection();
    con.setConnectionRouter(manhattanRouter);
    return con;
  }
  
  /**
   * <p>
   * Convenience Factory method.
   * </p>
   * <p>
   * Calls {@link #createIFigureEndConnection(int)} with the argument
   * {@link SWT#LINE_SOLID}.
   * </p>
   * 
   * @return {@link PolylineConnection}
   */
  public static PolylineConnection createConnection() {
    return createConnection(SWT.LINE_SOLID);
  }
  
  /**
   * <p>
   * Factory method.
   * </p>
   * <p>
   * Create a connection figure that is able to handle bendpoints, by using a
   * {@link BendpointConnectionRouter}.<br>
   * In order to handle collisions of two overlapping connections, a
   * {@link FanRouter} is used as first router, before the
   * {@link BendpointConnectionRouter} is used.
   * </p>
   * 
   * @param lineStyle The style of the connection. Use
   *          <ul>
   *          <li>{@link SWT#LINE_CUSTOM}</li>
   *          <li>{@link SWT#LINE_DASH}</li>
   *          <li>{@link SWT#LINE_DASHDOT}</li>
   *          <li>{@link SWT#LINE_DASHDOTDOT}</li>
   *          <li>{@link SWT#LINE_DOT}</li>
   *          <li>{@link SWT#LINE_SOLID} (default)</li>
   *          </ul>
   * @return {@link PolylineConnection} for Connections
   */
  public static PolylineConnection createConnection(int lineStyle) {
    check();
    return factory.doCreateConnection(lineStyle);
  }
  
  /**
   * Inner Factory method.
   * 
   * @return {@link PolylineConnection}
   */
  protected PolylineConnection doCreateConnection(int lineStyle) {
    PolylineConnection con = new PolylineConnection();
    fanRouter.setNextRouter(new BendpointConnectionRouter());
    con.setConnectionRouter(fanRouter);
    con.setLineStyle(lineStyle);
    return con;
  }
}
