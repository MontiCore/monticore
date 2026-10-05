/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import javax.annotation.Nonnull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Computes a creation order for {@link ASTODObject}s based on their links.
 * Children are ordered before their parents so dependent objects already
 * exist when parent objects are constructed.
 *
 * <p>The class builds an internal graph keyed by object name and then performs
 * a depth-first traversal to derive a deterministic order.
 */
public class ODBuildOrder {

    /**
     * Internal tree/graph structure over OD objects and their links.
     *
     * <p>Nodes are kept in insertion order to preserve reproducible traversal
     * and output ordering.
     */
    public static class ObjectTrees {
        private final LinkedHashMap<String, ObjectNode> nodes = new LinkedHashMap<>();

        /**
         * Internal node representing one OD object.
         */
        public static class ObjectNode {
            private ObjectNode parent;
            private final List<ObjectNode> children = new LinkedList<>();
            private final ASTODObject object;
            private boolean visited = false;
            private final List<ASTODLink> attrs = new LinkedList<>();

            /**
             * @param object represented AST object
             */
            public ObjectNode(@Nonnull ASTODObject object) {
                this.object = object;
            }

        }

        /**
         * Adds all objects as nodes (duplicates by name are ignored).
         *
         * <p>If multiple objects share the same name, only the first occurrence
         * is stored.
         *
         * @param objects OD objects
         */
        public void addObjects(@Nonnull List<ASTODObject> objects) {
            for(ASTODObject o : objects) {
                if(!nodes.containsKey(o.getName())) {
                    ObjectNode node = new ObjectNode(o);
                    nodes.put(o.getName(), node);
                }
            }
        }

        /**
         * Connects existing nodes according to links and collects the associated
         * attributes on the parent node.
         *
         * <p>Links to unknown objects are tolerated: parent/child references are
         * only established for objects that exist in this tree.
         *
         * @param links OD links
         */
        public void addLinks(@Nonnull List<ASTODLink> links){
            for(ASTODLink l : links) {
                String parent = l.getLeftReferenceName(0).toString();
                String child = l.getRightReferenceName(0).toString();
                ObjectNode parentNode = nodes.get(parent);
                ObjectNode childNode = nodes.get(child);
                if (parentNode != null) {
                    if(childNode != null) {
                        parentNode.children.add(childNode);
                    }
                    parentNode.attrs.add(l);
                }
                if (childNode != null) {
                    childNode.parent = parentNode;
                }
            }
        }
    }

    private final ObjectTrees trees = new ObjectTrees();
    private final List<ASTODObject> buildOrder = new LinkedList<>();

    /**
     * Creates a new build-order calculator and immediately computes the order.
     *
     * <p>The provided lists are interpreted as the complete input snapshot for
     * this instance.
     *
     * @param objects all relevant OD objects
     * @param links links between the objects
     */
    public ODBuildOrder(@Nonnull List<ASTODObject> objects, @Nonnull List<ASTODLink> links) {
        trees.addObjects(objects);
        trees.addLinks(links);
        calculateBuildOrder();
    }

    /**
     * Returns the associated links/attributes for each object in build order.
     *
     * <p>The returned multimap follows the same iteration order as
     * {@link #getBuildOrder()}.
     *
     * @return ordered mapping object -> links
     */
    @Nonnull
    public Multimap<ASTODObject, ASTODLink> getBuildAttrs() {
        Multimap<ASTODObject, ASTODLink> buildAttrs = LinkedListMultimap.create();

        for(ASTODObject o : buildOrder) {
            ObjectTrees.ObjectNode node = trees.nodes.get(o.getName());
            if (node == null) {
                throw new IllegalStateException("Missing node for object in build order: " + o.getName());
            }
            buildAttrs.putAll(o, node.attrs);
        }

        return buildAttrs;
    }

    /**
     * Returns the current creation order.
     *
     * <p>The returned list is backed by the internal state of this instance.
     *
     * @return the computed creation order
     */
    @Nonnull
    public List<ASTODObject> getBuildOrder() {
        return buildOrder;
    }

    /**
     * Recomputes the build order and discards previous results.
     *
     * <p>This traversal marks nodes as visited and appends each node after all
     * of its children have been processed.
     */
    public void calculateBuildOrder() {
        for(ObjectTrees.ObjectNode node : trees.nodes.values()) {
            if(!node.visited) {
                ObjectTrees.ObjectNode localRoot = getRoot(node);
                calculateBuildOrder(localRoot);
            }
        }
    }

    private void calculateBuildOrder(@Nonnull ObjectTrees.ObjectNode node) {
        for(ObjectTrees.ObjectNode c : node.children) {
            calculateBuildOrder(c);
        }
        buildOrder.add(node.object);
        node.visited = true;
    }

    /**
     * Resolves the root node of the local parent chain.
     *
     * @param node any node
     * @return root node of the local structure
     */
    @Nonnull
    private ObjectTrees.ObjectNode getRoot(@Nonnull ObjectTrees.ObjectNode node) {
        return node.parent == null ? node : getRoot(node.parent);
    }


}
