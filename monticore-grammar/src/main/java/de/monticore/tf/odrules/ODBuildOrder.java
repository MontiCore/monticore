/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ODBuildOrder {

    public class ObjectTrees {
        private HashMap<String, ObjectNode> nodes = new HashMap<>();

        public class ObjectNode {
            private ObjectNode parent;
            private List<ObjectNode> childs = new LinkedList<>();
            private ASTODObject object;
            private boolean visited = false;
            private List<ASTODLink> attrs = new LinkedList<>();

            public ObjectNode(ASTODObject object) {
                this.object = object;
            }

        }

        public void addObjects(List<ASTODObject> objects) {
            for(ASTODObject o : objects) {
                if(!nodes.containsKey(o.getName())) {
                    ObjectNode node = new ObjectNode(o);
                    nodes.put(o.getName(), node);
                }
            }
        }

        public void addLinks(List<ASTODLink> links){
            for(ASTODLink l : links) {
                String parent = l.getLeftReferenceName(0).toString();
                String child = l.getRightReferenceName(0).toString();
                ObjectNode parentNode = nodes.get(parent);
                ObjectNode childNode = nodes.get(child);
                if (parentNode != null) {
                    if(childNode != null) {
                        parentNode.childs.add(childNode);
                    }
                    parentNode.attrs.add(l);
                }
                if (childNode != null) {
                    childNode.parent = parentNode;
                }
            }
        }
    }

    private ObjectTrees trees = new ObjectTrees();
    private List<ASTODObject> buildOrder = new LinkedList<>();


    public ODBuildOrder(List<ASTODObject> objects, List<ASTODLink> links) {
        trees.addObjects(objects);
        trees.addLinks(links);
        calculateBuildOrder();
    }

    public HashMap<ASTODObject, List<ASTODLink>> getBuildAttrs() {
        HashMap<ASTODObject, List<ASTODLink>> buildAttrs = new HashMap<>();

        for(ASTODObject o : buildOrder) {
            buildAttrs.put(o, trees.nodes.get(o.getName()).attrs);
        }

        return buildAttrs;
    }

    public List<ASTODObject> getBuildOrder() {
        return buildOrder;
    }

    public void calculateBuildOrder() {
        for(ObjectTrees.ObjectNode node : trees.nodes.values()) {
            if(!node.visited) {
                ObjectTrees.ObjectNode localRoot = getRoot(node);
                calculateBuildOrder(localRoot);
            }
        }
    }

    private void calculateBuildOrder(ObjectTrees.ObjectNode node) {
        for(ObjectTrees.ObjectNode c : node.childs) {
            calculateBuildOrder(c);
        }
        buildOrder.add(node.object);
        node.visited = true;
    }

    private ObjectTrees.ObjectNode getRoot(ObjectTrees.ObjectNode node) {
        return node.parent == null ? node : getRoot(node.parent);
    }


}
