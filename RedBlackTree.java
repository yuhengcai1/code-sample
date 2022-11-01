import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Stack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Red-Black Tree implementation with a Node inner class for representing
 * the nodes of the tree. Currently, this implements a Binary Search Tree that
 * we will turn into a red black tree by modifying the insert functionality.
 * In this activity, we will start with implementing rotations for the binary
 * search tree insert algorithm. You can use this class' insert method to build
 * a regular binary search tree, and its toString method to display a level-order
 * traversal of the tree.
 */
public class RedBlackTree<T extends Comparable<T>> {

    /**
     * This class represents a node holding a single value within a binary tree
     * the parent, left, and right child references are always maintained.
     */
    protected static class Node<T> {
        public T data;
        public Node<T> parent; // null for root node
        public Node<T> leftChild;
        public Node<T> rightChild;
        public int blackHeight;
        public Node(T data) { this.data = data; 
        this.blackHeight = 0 ;}
        /**
         * @return true when this node has a parent and is the left child of
         * that parent, otherwise return false
         */
        public boolean isLeftChild() {
            return parent != null && parent.leftChild == this;
        }

    }

    protected Node<T> root; // reference to root node of tree, null when empty
    protected int size = 0; // the number of values in the tree

    /**
     * Performs a naive insertion into a binary search tree: adding the input
     * data value to a new node in a leaf position within the tree. After  
     * this insertion, no attempt is made to restructure or balance the tree.
     * This tree will not hold null references, nor duplicate data values.
     * @param data to be added into this binary search tree
     * @return true if the value was inserted, false if not
     * @throws NullPointerException when the provided data argument is null
     * @throws IllegalArgumentException when the newNode and subtree contain
     *      equal data references
     */
    public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
        // null references cannot be stored within this tree
        if(data == null) throw new NullPointerException(
                "This RedBlackTree cannot store null references.");

        Node<T> newNode = new Node<>(data);
        if(root == null) { root = newNode; size++; root.blackHeight = 1; return true; } // add first node to an empty tree
        else{
            boolean returnValue = insertHelper(newNode,root); // recursively insert into subtree
            if (returnValue) size++;
            else throw new IllegalArgumentException(
                    "This RedBlackTree already contains that value.");
            if(root.blackHeight == 0){
                root.blackHeight = 1;
            }
            return returnValue;
        }
    }

    /**
     * Recursive helper method to find the subtree with a null reference in the
     * position that the newNode should be inserted, and then extend this tree
     * by the newNode in that position.
     * @param newNode is the new node that is being added to this tree
     * @param subtree is the reference to a node within this tree which the 
     *      newNode should be inserted as a descenedent beneath
     * @return true is the value was inserted in subtree, false if not
     */
    private boolean insertHelper(Node<T> newNode, Node<T> subtree) {
        int compare = newNode.data.compareTo(subtree.data);
        // do not allow duplicate values to be stored within this tree
        if(compare == 0) return false;

            // store newNode within left subtree of subtree
        else if(compare < 0) {
            if(subtree.leftChild == null) { // left subtree empty, add here
                subtree.leftChild = newNode;
                newNode.parent = subtree;
                enforceRBTreePropertiesAfterInsert(newNode);
                return true;
                // otherwise continue recursive search for location to insert
            } else return insertHelper(newNode, subtree.leftChild);
        }

        // store newNode within the right subtree of subtree
        else {
            if(subtree.rightChild == null) { // right subtree empty, add here
                subtree.rightChild = newNode;
                newNode.parent = subtree;
                enforceRBTreePropertiesAfterInsert(newNode);
                return true;
                // otherwise continue recursive search for location to insert
            } else return insertHelper(newNode, subtree.rightChild);
        }
    }

    /**
     * Performs the rotation operation on the provided nodes within this tree.
     * When the provided child is a leftChild of the provided parent, this
     * method will perform a right rotation. When the provided child is a
     * rightChild of the provided parent, this method will perform a left rotation.
     * When the provided nodes are not related in one of these ways, this method
     * will throw an IllegalArgumentException.
     * @param child is the node being rotated from child to parent position
     *      (between these two node arguments)
     * @param parent is the node being rotated from parent to child position
     *      (between these two node arguments)
     * @throws IllegalArgumentException when the provided child and parent
     *      node references are not initially (pre-rotation) related that way
     */
    private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
        if(child == parent.leftChild){
        Node<T> tem = child.rightChild;
        Node<T> parpar = parent.parent;
        Node<T> tempar = parent;
        Node<T> temchi = child;
        boolean isLeftChild = parent.isLeftChild();
        if (tem != null) {
            tem.parent = tempar;
        }
        tempar.leftChild = tem;
        tempar.parent = temchi;
        temchi.rightChild = tempar;
        temchi.parent = parpar;
        if (parpar != null && isLeftChild) {
            parpar.leftChild = temchi;
        } else if (parpar != null) {
            parpar.rightChild = temchi;
        } else {
            this.root = temchi;
        }
        }else if(child  == parent.rightChild){
            Node<T> tem = child.leftChild;
            Node<T> parpar = parent.parent;
            Node<T> tempar = parent;
            Node<T> temchi = child;
            boolean isLeftChild = parent.isLeftChild();
            if (tem != null) {
                tem.parent = tempar;
            }
            tempar.rightChild = tem;
            tempar.parent = temchi;
            temchi.leftChild = tempar;
            temchi.parent = parpar;
            if (parpar != null && isLeftChild) {
                parpar.leftChild = temchi;
            } else if (parpar != null) {
                parpar.rightChild = temchi;
            } else {
                this.root = temchi;
            }

        }else{
            throw new IllegalAccessError("there are not related");
        }
    }


    /**
     * Get the size of the tree (its number of nodes).
     * @return the number of nodes in the tree
     */
    public int size() {
        return size;
    }

    /**
     * Method to check if the tree is empty (does not contain any node).
     * @return true of this.size() return 0, false if this.size() > 0
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Checks whether the tree contains the value *data*.
     * @param data the data value to test for
     * @return true if *data* is in the tree, false if it is not in the tree
     */
    public boolean contains(T data) {
        // null references will not be stored within this tree
        if(data == null) throw new NullPointerException(
                "This RedBlackTree cannot store null references.");
        return this.containsHelper(data, root);
    }

    /**
     * Recursive helper method that recurses through the tree and looks
     * for the value *data*.
     * @param data the data value to look for
     * @param subtree the subtree to search through
     * @return true of the value is in the subtree, false if not
     */
    private boolean containsHelper(T data, Node<T> subtree) {
        if (subtree == null) {
            // we are at a null child, value is not in tree
            return false;
        } else {
            int compare = data.compareTo(subtree.data);
            if (compare < 0) {
                // go left in the tree
                return containsHelper(data, subtree.leftChild);
            } else if (compare > 0) {
                // go right in the tree
                return containsHelper(data, subtree.rightChild);
            } else {
                // we found it :)
                return true;
            }
        }
    }


    /**
     * This method performs an inorder traversal of the tree. The string 
     * representations of each data value within this tree are assembled into a
     * comma separated string within brackets (similar to many implementations 
     * of java.util.Collection, like java.util.ArrayList, LinkedList, etc).
     * Note that this RedBlackTree class implementation of toString generates an
     * inorder traversal. The toString of the Node class class above
     * produces a level order traversal of the nodes / values of the tree.
     * @return string containing the ordered values of this tree (in-order traversal)
     */
    public String toInOrderString() {
        // generate a string of all values of the tree in (ordered) in-order
        // traversal sequence
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        sb.append(toInOrderStringHelper("", this.root));
        if (this.root != null) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(" ]");
        return sb.toString();
    }

    private String toInOrderStringHelper(String str, Node<T> node){
        if (node == null) {
            return str;
        }
        str = toInOrderStringHelper(str, node.leftChild);
        str += (node.data.toString() + ", ");
        str = toInOrderStringHelper(str, node.rightChild);
        return str;
    }

    /**
     * This method performs a level order traversal of the tree rooted
     * at the current node. The string representations of each data value
     * within this tree are assembled into a comma separated string within
     * brackets (similar to many implementations of java.util.Collection).
     * Note that the Node's implementation of toString generates a level
     * order traversal. The toString of the RedBlackTree class below
     * produces an inorder traversal of the nodes / values of the tree.
     * This method will be helpful as a helper for the debugging and testing
     * of your rotation implementation.
     * @return string containing the values of this tree in level order
     */
    public String toLevelOrderString() {
        String output = "[ ";
        if (this.root != null) {
            LinkedList<Node<T>> q = new LinkedList<>();
            q.add(this.root);
            while(!q.isEmpty()) {
                Node<T> next = q.removeFirst();
                if(next.leftChild != null) q.add(next.leftChild);
                if(next.rightChild != null) q.add(next.rightChild);
                output += next.data.toString();
                if(!q.isEmpty()) output += ", ";
            }
        }
        return output + " ]";
    }

    public String toString() {
        return "level order: " + this.toLevelOrderString() +
                "\nin order: " + this.toInOrderString();
    }


    protected void enforceRBTreePropertiesAfterInsert(Node<T> cur){
        if(cur.parent == null || cur.blackHeight == 1){
            // if the cur is root
            cur.blackHeight = 1;
            return;
        }
        if(cur.parent.blackHeight == 1){
            // if the parent is black
            return ;
        }
        if(cur.parent.blackHeight == 0){
            Node<T> curparent = cur.parent;
            boolean isleft = curparent.isLeftChild();
            if(curparent.parent == null){
                // if the parent is root
                curparent.blackHeight = 1;
                return;
            }else{
                Node<T> gradparent = curparent.parent;
                Node<T> uncle = null;
                if(isleft){
                    uncle = gradparent.rightChild;
                }else{
                    uncle = gradparent.leftChild;
                }
                if(uncle == null){
                    if((cur.isLeftChild() && !curparent.isLeftChild()) ||(!cur.isLeftChild() && curparent.isLeftChild())){
                        // if the parent and child and grandfather and parent in the opposite side
                        rotate(cur, curparent);
                        // set them to the same side
                        Node<T> tem = curparent;
                        curparent = cur;
                        cur = tem;
                    }
                    rotate(curparent, gradparent);
                    // rotate for the first parent and grad parent and change there color 
                    curparent.blackHeight = 1;
                    gradparent.blackHeight = 0;
                    return;
                }
                if(uncle.blackHeight == 1){
                    if((cur.isLeftChild() && !curparent.isLeftChild()) ||(!cur.isLeftChild() && curparent.isLeftChild())){
                        // if the parent and child and grandfather and parent in the same side
                        rotate(cur, curparent);
                        Node<T> tem = curparent;
                        curparent = cur;
                        cur = tem;
                    }
                    rotate(curparent, gradparent);
                    // rotate for the first parent and grad parent and change there color 
                    curparent.blackHeight = 1;
                    gradparent.blackHeight = 0;
                }
                if(uncle.blackHeight == 0){
                    // the brother is red, we just need to change the color
                    curparent.blackHeight = 1;
                    uncle.blackHeight = 1;
                    gradparent.blackHeight = 0;
                    enforceRBTreePropertiesAfterInsert(gradparent);
                    // check about if there is violation for grad parents
                }
            }
        }
        

    }

    
    // Implement at least 3 boolean test methods by using the method signatures below,
    // removing the comments around them and addind your testing code to them. You can
    // use your notes from lecture for ideas on concrete examples of rotation to test for.
    // Make sure to include rotations within and at the root of a tree in your test cases.
    // If you are adding additional tests, then name the method similar to the ones given below.
    // Eg: public static boolean test4() {}
    // Do not change the method name or return type of the existing tests.
    // You can run your tests by commenting in the calls to the test methods 

    @Test
    /**
     * test about whether the root will stay in 1 for each time
     */
    public void test1() {
        RedBlackTree<Integer> test1 = new RedBlackTree<>();
        for (int i = 0; i < 10; i++) {
            test1.insert(i);
            assertEquals(1,test1.root.blackHeight);
        }
    }

    /**
     * Test for random add element into the rb tree and have duplicates
     */
    @Test
    public void test2() {
        RedBlackTree<Integer> test2 = new RedBlackTree<>();
        test2.insert(2);
        test2.insert(4);
        test2.insert(1);
        test2.insert(8);
        test2.insert(5);
        test2.insert(123);
        test2.insert(213);
        assertEquals("level order: [ 2, 1, 5, 4, 123, 8, 213 ]\nin order: [ 1, 2, 4, 5, 8, 123, 213 ]", test2.toString());
    }

    /**
     * test with more retation and call of the father notes
     */
    @Test
    public void test3() {
        RedBlackTree<Integer> test3 = new RedBlackTree<>();
        for (int i = 1; i <= 10; i++) {
            test3.insert(i);
        }
        assertEquals("level order: [ 4, 2, 6, 1, 3, 5, 8, 7, 9, 10 ]\nin order: [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]", test3.toString());
    }

    /**
     * test for the nodes insert in reverse order
     */
    @Test
    public void test4() {
        RedBlackTree<Integer> test4 = new RedBlackTree<>();
        for (int i = 10; i > 0; i--) {
            test4.insert(i);
        }
        assertEquals("level order: [ 7, 5, 9, 3, 6, 8, 10, 2, 4, 1 ]\nin order: [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]", test4.toString());
    }

}

