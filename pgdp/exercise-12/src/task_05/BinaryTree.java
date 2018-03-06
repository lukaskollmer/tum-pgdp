package task_05;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;



// generic binary tree
public class BinaryTree<T extends Comparable<T>> {

    private Node<T> root;

    public BinaryTree() {

    }


    // Element insertion

    void insert(T element) {
        if (root == null) {
            root = new Node<>(element, null);
            root.pos = 0;
            return;
        }

        insert(element, root);
    }


    synchronized void insert(T element, Node<T> node) {

        switch (compare(element, node)) {
            case Ascending: {
                if (node.getLeftChild() != null) {
                    insert(element, node.getLeftChild());
                } else {
                    node.setLeftChild(new Node<>(element, node));
                    node.getLeftChild().pos = node.pos * 2;
                }
                break;
            }
            case Same: // fallthrough to descending
            case Descending: {
                if (node.getRightChild() != null) {
                    insert(element, node.getRightChild());
                } else {

                    node.setRightChild(new Node<>(element, node));
                    node.getRightChild().pos = node.pos * 2 + 1;
                }
                break;
            }
            default: break;
        }
    }


    // Element Removal

    synchronized void remove(T element) {
        if (!contains(element)) throw new RuntimeException("Trying to remove non-existent element");
        remove(element, root);
    }


    synchronized void remove(T element, Node<T> currentNode) {
        if (currentNode == null) {
            return;
        }

        switch (compare(element, currentNode)) {
            case Same: {

                if (currentNode == root) {
                    // about to delete the element in the root node

                    List<T> allElements = getElements(false);
                    allElements.remove(0);

                    this.root = null;

                    for (T value : allElements) {
                        this.insert(value);
                    }

                    return;
                }


                Node<T> parent = currentNode.getParent();

                boolean hasNoChildren  = currentNode.getLeftChild() == null && currentNode.getRightChild() == null;

                if (hasNoChildren) {
                    // check whether the current node is the left or right child od its parent and delete the correct reference

                    if (compare(element, parent.getLeftChild()) == ComparisonResult.Same) {
                        parent.setLeftChild(null);
                        return;

                    } else if (compare(element, parent.getRightChild()) == ComparisonResult.Same) {
                        parent.setRightChild(null);
                        return;
                    }

                    // should never reach here
                    return;

                } else {


                    List<T> childValues = new ArrayList<>();

                    traverse(currentNode, (node, depth, position) -> childValues.add(node.value));

                    Collections.reverse(childValues);


                    if (Collections.frequency(childValues, element) > 1) {

                        AtomicInteger maximumDepth = new AtomicInteger(-1);
                        AtomicReference<Node<T>> deepestOccurrence = new AtomicReference<>();

                        traverse(currentNode, ((node, depth, position) -> {
                            if (node.value.equals(element) && depth > maximumDepth.get()) {
                                maximumDepth.set(depth);
                                deepestOccurrence.set(node);
                            }
                        }));

                        this.remove(element, deepestOccurrence.get());
                        return;
                    }


                    for (T value : childValues) {
                        if (contains(value, currentNode)) this.remove(value, currentNode);
                    }

                    Collections.reverse(childValues);

                    childValues.remove(0);

                    for (T value : childValues) {
                        this.insert(value, parent);
                    }
                }
                break;
            }
            case Ascending: {
                remove(element, currentNode.getLeftChild());
                break;
            }
            case Descending: {
                remove(element, currentNode.getRightChild());
                break;
            }
        }
    }


    // Contains

    boolean contains(T element) {
        return contains(element, root);
    }


    boolean contains(T element, Node<T> currentNode) {
        if (currentNode == null) {
            return false;
        }

        switch (compare(element, currentNode)) {
            case Same:
                return true;
            case Ascending:
                return contains(element, currentNode.getLeftChild());
            case Descending:
                return contains(element, currentNode.getRightChild());
        }

        return false;
    }



    // get the maximum depth of the entire tree
    int getMaximumDepth() {
        return getMaximumDepth(root);
    }

    // get the maximum depth, starting at `node`
    int getMaximumDepth(Node<T> node) {
        return getMaximumDepth(node, 0);
    }

    // actual implementation
    int getMaximumDepth(Node<T> node, int currentDepth) {

        AtomicInteger maxDepth = new AtomicInteger(currentDepth);

        traverse(node, ((_node, depth, position) -> maxDepth.set(Math.max(maxDepth.get(), depth))));

        return maxDepth.get();
    }


    //
    // Tree Traversal
    //


    interface TraversalHandler<T> {
        void handle(Node<T> node, int depth, int position);
    }

    // traverse the tree, starting at `node`
    synchronized void traverse(Node<T> node, TraversalHandler<T> handler) {
        traverse(node, 0, handler);
    }

    // tree traversal implementation
    synchronized void traverse(Node<T> node, int depth, TraversalHandler<T> handler) {
        if (node == null) {
            return;
        }

        handler.handle(node, depth, node.pos);

        if (node.getLeftChild() == null && node.getRightChild() == null) {
            return;
        }

        traverse(node.getLeftChild(), depth + 1, handler);
        traverse(node.getRightChild(), depth + 1, handler);
    }




    //
    // Element access
    //

    // Get all elements, as a c-style array
    // Note: Since `BinaryTree` is generic, we need to know the size of the generic parameter type in advance
    // This means that you need to pass an empty array of the generic type
    // eg (for BinaryTree<Integer>):
    //   Integer[] elem = tree.getElementsAsArray(new Integer[0]);
    T[] getElementsAsArray(T[] a) {
        return getElements(true).toArray(a);
    }


    List<T> getElementsAtLevel(int level, boolean includingNullValues) {
        if (level < 0 || level > getMaximumDepth()) {
            throw new ArrayIndexOutOfBoundsException(String.format("Level %s does not exist!", level));
        }


        List<T> allElements = getElements(true);

        int startIndex = BinaryTree.Utils.getNumberOfNodesUpToLevel(level - 1);
        int endIndex = startIndex + BinaryTree.Utils.getNumberOfNodesOnLevel(level);

        List<T> elementsAtLevel = allElements.subList(startIndex, endIndex);

        if (!includingNullValues) {
            elementsAtLevel = elementsAtLevel
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return elementsAtLevel;
    }


    List<T> getElements(boolean includingNullValues) {
        List<T> elements = new ArrayList<>();

        if (!includingNullValues) {
            traverse(root, (node, depth, position) -> elements.add(node.value));
            return elements;
        }


        int totalNumberOfNodes = BinaryTree.Utils.getTotalNumberOfNodes(getMaximumDepth());

        for (int i = 0; i < totalNumberOfNodes; i++) {
            elements.add(null);
        }

        traverse(root, (node, depth, position) -> {
            int index = depth + position;

            if (depth > 1) {
                index = BinaryTree.Utils.getNumberOfNodesUpToLevel(depth-1) + position;
            }

            elements.set(index, node.value);
        });

        return elements;
    }





    static class Node<T> {
        // keep a weak reference to the parent node
        private WeakReference<Node<T>> parent;

        private Node<T> leftChild;
        private Node<T> rightChild;

        // position of the node on its level in the tree
        // (this is needed to properly fill in null values when getting all elements of the tree)
        private int pos;

        private T value;

        Node(T value, Node<T> parent) {
            this.value = value;
            setParent(parent);
        }

        void setLeftChild(Node<T> leftChild) {
            this.leftChild = leftChild;

            if (this.leftChild != null) {
                this.leftChild.setParent(this);
            }
        }

        void setRightChild(Node<T> rightChild) {
            this.rightChild = rightChild;

            if (this.rightChild != null) {
                this.rightChild.setParent(this);
            }
        }

        void setParent(Node<T> parent) {
            this.parent = new WeakReference<>(parent);
        }


        Node<T> getLeftChild() {
            return this.leftChild;
        }

        Node<T> getRightChild() {
            return this.rightChild;
        }


        Node<T> getParent() {
            return this.parent.get();
        }


        @Override
        public String toString() {
            return String.format("<Node value=%s pos=%s>", this.value, this.pos);
        }
    }


    // BinaryTree utils
    static class Utils {

        // get the number of nodes in all levels between 0 and level
        // eg: for level 1 this would return 3 (1 node in level 0 + 2 in level 1)
        static int getNumberOfNodesUpToLevel(int level) {
            if (level < 0) {
                return 0;
            } else if (level == 0) {
                return 1;
            }

            return (int) (Math.pow(2, level) + getNumberOfNodesUpToLevel(level - 1));
        }


        static int getNumberOfNodesOnLevel(int level) {
            return (int) Math.pow(2, level);
        }


        // get the total number of nodes in the tree (including empty ones)
        static int getTotalNumberOfNodes(int maximumDepth) {
            return (int) (Math.pow(2, maximumDepth + 1) - 1);
        }
    }



    //
    // Comparisons
    //

    enum ComparisonResult {
        Ascending,
        Same,
        Descending,
        Undefined
    }


    // compare two nodes
    ComparisonResult compare(Node<T> left, Node<T> right) {
        if (left == null || right == null) {
            return ComparisonResult.Undefined;
        }
        return compare(left.value, right.value);
    }


    // compare a value and a node
    ComparisonResult compare(T element, Node<T> node) {
        if (element == null || node == null) {
            return ComparisonResult.Undefined;
        }
        return compare(element, node.value);
    }


    // compare two values
    ComparisonResult compare(T left, T right) {
        if (left == null || right == null) {
            return ComparisonResult.Undefined;
        }

        int cmp = left.compareTo(right);
        if (cmp < 0) {
            return ComparisonResult.Ascending;
        } else if (cmp == 0) {
            return ComparisonResult.Same;
        } else {
            return ComparisonResult.Descending;
        }
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("digraph NAME {\n");

        traverse(root, (node, ___ignored0, ___ignored1) -> {
            // 0 - add the node
            stringBuilder.append(String.format("  %s;\n", node.value));

            if (node.getParent() == null) return; // root doesn't have a parent (obviously)

            // 1 - add the connection to the parent
            Consumer<String> drawConnection = label -> stringBuilder.append(String.format("  %s -> %s [label=%s];\n", node.getParent().value, node.value, label));
            drawConnection.accept(compare(node, node.getParent().getLeftChild()) == ComparisonResult.Same ? "left" : "right");
        });

        return stringBuilder.append("}").toString();
    }
}
