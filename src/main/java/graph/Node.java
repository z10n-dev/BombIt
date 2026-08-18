package graph;

public class Node {
    private final int x;
    private final int y;
    private Node next;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setNext(Node n) {
        this.next = n;
    }

    public Node getNext() {
        return next;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
