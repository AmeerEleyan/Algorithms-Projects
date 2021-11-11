package huffman;

import java.io.*;
import java.util.PriorityQueue;
import java.util.Stack;

public class HuffmanCompress {
    private static final int ALPHABET_SIZE = 256;
    public static StatisticsTable[] huffmanTable;
    private static Node root;
    public static String header = "";
    private static short numberOfNodes = 0;


    public static void compress(final File sourceFile) {
        root = buildHuffmanTree(buildFrequenciesOfTheBytes(sourceFile));
        assert root != null;
        huffmanTable = new StatisticsTable[ALPHABET_SIZE];
        getHuffmanCode(root, "");
        printToFile(sourceFile);

        // System.out.println(numberOfNodes);
        // printPostorder(root);

//        System.out.print(Arrays.toString(in));
//        System.out.println("*/**********");
//        System.out.print(Arrays.toString(p));
//        inorderTraversal(root);
//        System.out.println("**********");
//        preorderTraversal(root);

//        iterativePreorder(root);
//        byte[] pre = {0, 97, 0, 0, 0, 100, 102, 98, 0, 99, 101};
//        char[] preL = {'N', 'L', 'N', 'N', 'N', 'L', 'L', 'L', 'N', 'L', 'N'};
//        Node d = constructTree(pre.length, pre, preL);


    }

    public static int[] buildFrequenciesOfTheBytes(final File sourceFile) {
        int[] frequencies = new int[ALPHABET_SIZE];
        try {
            FileInputStream fis = new FileInputStream(sourceFile);

            byte[] buffer = new byte[1024]; // number of bytes can be read

            // remaining is the number of bytes to read to fill the buffer
            short remaining = (short) buffer.length;

            while (true) {
                int read = fis.read(buffer, buffer.length - remaining, remaining);
                if (read >= 0) { // some bytes were read
                    remaining -= read;
                    if (remaining == 0) { // the buffer is full
                        for (byte b : buffer) {
                            frequencies[b + 128]++;
                        }
                        remaining = (short) buffer.length;
                    }
                } else {
                    // the end of the sourceFile was reached. If some bytes are in the buffer

                    for (int i = 0; i < buffer.length - remaining; i++) {
                        frequencies[buffer[i] + 128]++;
                    }

                    break;
                }
            }
            fis.close();
            return frequencies;
        } catch (IOException e) {
            Message.displayMessage("Warning", e.getMessage());
        }
        return null;
    }


    private static Node buildHuffmanTree(final int[] frequencies) {

        if (frequencies != null) {

            PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
            for (int i = 0; i < ALPHABET_SIZE; i++) { // start from -128 to the 127 because it's the range of th byte
                if (frequencies[i] > 0) {
                    priorityQueue.add(new Node((byte) (i - 128), frequencies[i], null, null));
                    numberOfNodes++;
                }
            }
            //numberOfNodes = (short) ((numberOfNodes * 2) - 1);

            while (priorityQueue.size() > 1) { // O(nlogn)
                Node left = priorityQueue.poll();
                Node right = priorityQueue.poll();
                assert right != null;
                Node parent = new Node((byte) '\0', left.getFrequency() + right.getFrequency(), left, right);
                priorityQueue.add(parent);

            }

            assert priorityQueue.peek() != null;
            return priorityQueue.peek();
        }
        return null;
    }

    // build huffman code for each byte recursive
    public static void getHuffmanCode(Node node, String code) {
        if (node.isLeaf()) {
            huffmanTable[node.getBytes() + 128] = new StatisticsTable(node.getBytes(), node.getFrequency(), code, code.length());
        } else {
            getHuffmanCode(node.getLeftChild(), code + "0");
            getHuffmanCode(node.getRightChild(), code + "1");
        }
    }


    public static void printToFile(final File sourceFile) {

        byte indexOfDot = (byte) sourceFile.getAbsolutePath().lastIndexOf('.');
        String newFilePath = sourceFile.getAbsolutePath().substring(0, indexOfDot + 1) + "huf";
        String fileExtension = sourceFile.getAbsolutePath().substring(indexOfDot + 1);
        byte[] fileExtensionBytes = fileExtension.getBytes();
        int lengthOfFile = (int) sourceFile.length();
        try {

            // ******* print the extension to the destination file *********************
            FileOutputStream fos = new FileOutputStream(newFilePath);

            // print the file length in bytes ( 4 bytes)
            String l = Integer.toBinaryString(lengthOfFile);
            l = "0".repeat(32 - l.length()) + l;
            byte[] lengthInBytes = getFileLengthAsBytes(l);
            fos.write(lengthInBytes, 0, 4);


            // print the file extension
            fos.write(fileExtensionBytes, 0, fileExtensionBytes.length);
            fos.write('\n');


            //header = getFileLengthAsString(lengthInBytes); // file length
            // header += (fileExtension + "\n");// file extension


            // new
            byte[][] data = Osama.getHeader(huffmanTable, numberOfNodes);

            fos.write(data[0].length); // lengths
            fos.write(data[0], 0, data[0].length);

            fos.write(data[1].length); // bytes
            fos.write(data[1], 0, data[1].length);

            int data2length = data[2].length;
            String slength = Integer.toBinaryString(data2length);
            slength = "0".repeat(16 - slength.length()) + slength;
            byte[] tempByte = new byte[2];
            tempByte[0] = (byte) (int) Integer.valueOf(slength.substring(0, 8), 2);
            tempByte[1] = (byte) (int) Integer.valueOf(slength.substring(8), 2);

            fos.write(tempByte, 0, 2); /// huffman
            fos.write(data[2], 0, data2length);

            // print the huffman in inOrder traversal
           /* byte[] inorderTraversal = getInOrderTraversal(root, numberOfNodes);
            fos.write(inorderTraversal, 0, numberOfNodes);
            // ^` are special to separate in order from preOrder
            fos.write('^');
            fos.write('`');*/

            // print the huffman in preOrder traversal
           /* byte[] preOrderTraversal = getPreOrderTraversal(root, numberOfNodes);
            fos.write(preOrderTraversal, 0, numberOfNodes);
            // ^` are special to separate in preOrder from huffmanCode
            fos.write('^');
            fos.write('`');*/

            //header += getInOrderTraversalAsString(inorderTraversal);
            // header += getPreOrderTraversalAsString(preOrderTraversal);
            // ********** end of the header ********************


            // ************** encode the file and print to the output file ************

            InputStream fis = new FileInputStream(sourceFile);

            byte[] buffer = new byte[1024]; // number of bytes can be read

            // remaining is the number of bytes to read to fill the buffer
            short remaining = (short) buffer.length;

            byte[] huffman = new byte[1024];
            short index = 0; // used for the above huffman array
            String remainingBits = "";
            String huffmanBits = "";
            while (true) {
                short read = (short) fis.read(buffer, buffer.length - remaining, remaining);
                if (read >= 0) { // some bytes were read
                    remaining -= read;
                    if (remaining == 0) { // the buffer is full
                        for (byte b : buffer) {
                            huffmanBits = remainingBits + huffmanTable[b + 128].getHuffmanCode();

                            if (huffmanBits.length() >= 8) {
                                remainingBits = huffmanBits.substring(8); // to store bit above than index 7
                                byte huffmanByte = (byte) (int) Integer.valueOf(huffmanBits.substring(0, 8), 2);
                                huffman[index++] = huffmanByte;
                                if (index == 1024) {
                                    fos.write(huffman, 0, 1024);
                                    index = 0;
                                }
                            } else {
                                remainingBits = huffmanBits;
                            }

                        }
                        remaining = (short) buffer.length;
                    }
                } else {
                    // the end of the file was reached. If some bytes are in the buffer

                    for (int i = 0; i < buffer.length - remaining; i++) { // for the remaining bytes
                        huffmanBits = remainingBits + huffmanTable[buffer[i] + 128].getHuffmanCode();

                        if (huffmanBits.length() >= 8) {
                            remainingBits = huffmanBits.substring(8); // to store bit above than index 7
                            byte huffmanByte = (byte) (int) Integer.valueOf(huffmanBits.substring(0, 8), 2);
                            huffman[index++] = huffmanByte;
                            if (index == 1024) {
                                fos.write(huffman, 0, 1024);
                                index = 0;
                            }
                        } else {
                            remainingBits = huffmanBits;
                        }
                    }


                    String temp;
                    while (remainingBits.length() != 0) {
                        int length = remainingBits.length();
                        if (length < 8) {
                            temp = remainingBits.substring(length);
                            remainingBits += ("0".repeat(8 - length));
                        } else {
                            temp = remainingBits.substring(8);
                            remainingBits = remainingBits.substring(0, 8);
                        }
                        byte huffmanByte = (byte) (int) Integer.valueOf(remainingBits, 2);
                        huffman[index++] = huffmanByte;
                        if (index == 1024) {
                            fos.write(huffman, 0, 1024);
                            index = 0;
                        }
                        remainingBits = temp;
                    }
                    break;
                }
            }
            if (index > 0) {
                fos.write(huffman, 0, index);
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            Message.displayMessage("Warning", e.getMessage());
        }

    }

    public static void returnDefault() {
        huffmanTable = new StatisticsTable[ALPHABET_SIZE];
        root = null;
        header = "";
        numberOfNodes = 0;
    }

    private static byte[] getFileLengthAsBytes(String binaryString) {

        byte[] bytes = new byte[4]; // number of digits
        bytes[0] = (byte) (int) Integer.valueOf(binaryString.substring(0, 8), 2);
        bytes[1] = (byte) (int) Integer.valueOf(binaryString.substring(8, 16), 2);
        bytes[2] = (byte) (int) Integer.valueOf(binaryString.substring(16, 24), 2);
        bytes[3] = (byte) (int) Integer.valueOf(binaryString.substring(24, 32), 2);
        return bytes;
    }

    private static String getFileLengthAsString(byte[] length) {
        String len = "";
        for (byte b : length) {
            len += (char) b;
        }
        return len + "\n";
    }

    private static String byteToString(byte b) {
        byte[] masks = {-128, 64, 32, 16, 8, 4, 2, 1};
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }

    private static byte[] getInOrderTraversal(Node root, short numberOfLeaf) {
        Stack<Node> nodeStack = new Stack<>();
        Node current = root;
        byte[] inorder = new byte[numberOfLeaf];
        short index = 0;
        while (!nodeStack.isEmpty() || current != null) {
            if (current != null) {
                nodeStack.push(current);
                current = current.getLeftChild();
            } else {
                Node node = nodeStack.pop();
                inorder[index++] = node.getBytes();
                current = node.getRightChild();

            }
        }
        return inorder;
    }

    private static String getInOrderTraversalAsString(byte[] inorder) {
        String in = "";
        for (byte b : inorder) {
            if (b == '\0') in += ' ';
            else in += (char) b;
        }
        return in + "\n";
    }

    private static byte[] getPreOrderTraversal(Node root, short numberOfLeaf) {

        // Create an empty stack and push root to it
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);

        byte[] preorder = new byte[numberOfLeaf];
        short index = 0;

        /* Pop all items one by one. Do following for every popped item
        a) print it
        b) push its right child
        c) push its left child
        Note that right child is pushed first so that left is processed first */
        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node current = nodeStack.peek();
            preorder[index++] = current.getBytes();
            nodeStack.pop();

            // Push right and left children of the popped node to stack
            if (current.getRightChild() != null) {
                nodeStack.push(current.getRightChild());
            }
            if (current.getLeftChild() != null) {
                nodeStack.push(current.getLeftChild());
            }
        }
        return preorder;
    }

    private static String getPreOrderTraversalAsString(byte[] preorder) {
        String pre = "";
        for (byte b : preorder) {
            if (b == '\0') pre += ' ';
            else pre += (char) b;
        }
        return pre + "\n";
    }

//    static void printPostorder(Node node) {
//        if (node == null)
//            return;
//
//        // first recur on left subtree
//        printPostorder(node.getLeftChild());
//
//        // then recur on right subtree
//        printPostorder(node.getRightChild());
//
//        // now deal with the node
//        System.out.print(node.getBytes() + ", ");
//    }

//    public static Node constructTree(int n, byte pre[], char preLN[]) {
//
//        // Code here
//        Stack<Node> s = new Stack<>();
//        Node root = new Node(pre[0]);
//        s.push(root);
//        int i = 1;
//        while (i < n) {
//            Node curr = s.peek();
//            if (curr.getLeftChild() == null) {
//                curr.setLeftChild(new Node(pre[i]));
//                if (preLN[i] == 'N') {
//                    s.push(curr.getLeftChild());
//                }
//                i++;
//            } else if (curr.getRightChild() == null) {
//                curr.setRightChild(new Node(pre[i]));
//                if (preLN[i] == 'N') {
//                    s.push(curr.getRightChild());
//                }
//                i++;
//            } else {
//                s.pop();
//            }
//        }
//        return root;
//    }

}