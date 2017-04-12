package ie.dit;
/**
 * Prim's MST Algorithms Assignment
 *
 * This is the MST Algorithms Assignment for Algorithms and Data Structures. It implements
 * Prim's Algorithm.
 *
 * Author: Mark Barrett
 * Version: 1.0
 * Date: 27/03/2017
 */
import java.io.*;
import java.util.Scanner;

/* HEAP CLASS FOR PRIM */
class Heap
{
    private int[] h;	   // heap array
    private int[] hPos;	   // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v

    private int N;         // heap size

    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos)
    {
        N = 0;
        h = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }


    public boolean isEmpty()
    {
        return N == 0;
    }


    public void siftUp( int k)
    {
        int v = h[k];
        dist[0] = Integer.MIN_VALUE;

        while(dist[v] < dist[h[k/2]]) {
            h[k] = h[k/2];
            hPos[h[k]] = k;
            k = k/2;
        }
        h[k] = v;
        hPos[v] = k;
    }


    public void siftDown( int k)
    {
        int v, j;
        v = h[k];

        while(k <= N/2) {
            j = 2 * k;
            if(j+1 <= N && dist[h[j]] > dist[h[j+1]]) {
                ++j;
            }

            if(dist[h[j]] >= dist[v]) {
                break;
            }

            h[k] = h[j];
            hPos[h[k]] = hPos[h[j]];
            k = j;
            j = k*2;
        }
        h[k] = v;
        hPos[v] = k;
    }


    public void insert( int x)
    {
        h[++N] = x;
        siftUp(N);
    }


    public int remove()
    {
        int v = h[1];
        hPos[v] = 0; // v is no longer in heap
        h[N+1] = 0;  // put null node into empty spot

        h[1] = h[N--];
        siftDown(1);

        return v;
    }
}

/* EDGE CLASS FOR KRUSKAL */
class Edge {
    public int u, v, wgt;

    public Edge() {
        u = 0;
        v = 0;
        wgt = 0;
    }

    public Edge(int u, int v, int wgt) {
        this.u = u;
        this.v = v;
        this.wgt = wgt;
    }

    public void show() {
        System.out.print("Edge " + toChar(u) + "--" + wgt + "--" + toChar(v) + "\n") ;
    }

    // convert vertex into char for pretty printing
    private char toChar(int u)
    {
        return (char)(u + 64);
    }
}

/****************************************************
 *
 *       UnionFind partition to support union-find operations
 *       Implemented simply using Discrete Set Trees
 *
 *****************************************************/

class UnionFindSets
{
    private int[] treeParent;
    private int[] rank;
    private int N;

    public UnionFindSets(int V)
    {
        N = V;
        treeParent = new int[V+1];
        rank = new int[V+1];
        for (int i = 0; i < N; i++) {
            treeParent[i] = i;
            rank[i] = 0;
        }
    }

    public int findSet(int vertex)
    {
        while (vertex != treeParent[vertex]) {
            treeParent[vertex] = treeParent[treeParent[vertex]];    // path compression by halving
            vertex = treeParent[vertex];
        }
        return vertex;
    }

    public void union(int set1, int set2)
    {
        int xRoot = findSet(set1);
        int yRoot = findSet(set2);
        if (xRoot == yRoot) return;

        // make root of smaller rank point to root of larger rank
        if      (rank[xRoot] < rank[yRoot]) treeParent[xRoot] = yRoot;
        else if (rank[xRoot] > rank[yRoot]) treeParent[yRoot] = xRoot;
        else {
            treeParent[yRoot] = xRoot;
            rank[xRoot]++;
        }
    }

    public void showTrees()
    {
        int i;
        for(i=1; i<=N; ++i)
            System.out.print(toChar(i) + "->" + toChar(treeParent[i]) + "  " );
        System.out.print("\n");
    }

    public void showSets()
    {
        int u, root;
        int[] shown = new int[N+1];
        for (u=1; u<=N; ++u)
        {
            root = findSet(u);
            if(shown[root] != 1) {
                showSet(root);
                shown[root] = 1;
            }
        }
        System.out.print("\n");
    }

    private void showSet(int root)
    {
        int v;
        System.out.print("Set{");
        for(v=1; v<=N; ++v)
            if(findSet(v) == root)
                System.out.print(toChar(v) + " ");
        System.out.print("}  ");

    }

    private char toChar(int u)
    {
        return (char)(u + 64);
    }
}

class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;

    // create edge array
    Edge[] edge;

    // used for traversing graph
    private int[] visited;
    private int id;

    // default constructor
    public Graph(String graphFile) throws IOException
    {
        int u, v;
        int e, wgt;
        Node t;


        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +";  // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        edge = new Edge[E+1];

        /* THIS WILL CREATE THE NODES FOR PRIM */
        // create sentinel node
        z = new Node();
        z.next = z;

        // create adjacency lists, initialised to sentinel node z
        adj = new Node[V+1];

        for(v = 1; v <= V; ++v)
            adj[v] = z;

        /* THIS WILL CREATE THE EDGES FOR KRUSKAL */

        // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));

            // write code to put edge into adjacency matrix

            // Create a node to hold one connection
            Node n = new Node();
            n.vert = v;
            n.wgt = wgt;

            // Create a node to hold the second connection
            Node m = new Node();
            m.vert = u;
            m.wgt = wgt;


            // Add the connection to the left hand vertices list
            // If it points at the sentinel
            if (adj[v] == z) {
                adj[v] = m;
                m.next = z;
            } else {
                m.next = adj[v].next;
                adj[v].next = m;
            }

            // Add the connection to the right hand vertices list
            if (adj[u] == z) {
                adj[u] = n;
                n.next = z;
            }
            //If it doesn't point the sentinel
            else {
                //Attach to the first edge
                n.next = adj[u].next;
                adj[u].next = n;
            }

            /* Now that we have read the edge and made a node out of it for Prim, we can now make an edge object
            for it for Kruskal
             */

            // Create the object
            Edge newEdge = new Edge(u, v, wgt);

            // Insert the object
            edge[e] = newEdge;
        }

        System.out.println("\nPrinting edges for Kruskal\n");
        for(e=1; e<E; e++){
            edge[e].show();
        }

    }

    // convert vertex into char for pretty printing
    private char toChar(int u)
    {
        return (char)(u + 64);
    }

    // method to display the graph representation
    public void display() {
        int v;
        Node n;

        System.out.println("\nPrinting out the adjaceny matrix of lists for Prim");
        for(v=1; v<=V; ++v){
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + n.vert + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }



    /* PRIMS ALGORITHM */

    public void MST_Prim(int s)
    {
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist = new int[V+1];
        int[] parent = new int[V+1];
        int[] hPos = new int[V+1];
        Node t;

        for(int i=1; i<=V; ++i) {
            dist[i] = Integer.MAX_VALUE;
            parent[i] = 0;
            hPos[i] = 0;
        }

        //code here
        Heap pq =  new Heap(V, dist, hPos);
        pq.insert(s);

        dist[s] = 0;

        while(!pq.isEmpty())
        {
            v = pq.remove();
            wgt_sum = wgt_sum + dist[v];
            dist[v] = -dist[v];
            for(t = adj[v]; t != z; t = t.next) {
                if(t.wgt < dist[t.vert]) {
                    dist[t.vert] = t.wgt;
                    parent[t.vert] = v;

                    if(hPos[t.vert] == 0) {
                        pq.insert(t.vert);
                    }
                    else {
                        pq.siftUp(hPos[t.vert]);
                    }
                }
            }

        }
        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");

        mst = parent;

        showMST();

    }


    public void showMST()
    {
        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for(int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " -> " + toChar(mst[v]));
        System.out.println("");
    }

    /* QUICK SORT ALGORITHM TO SORT EDGES FOR KRUSKAL
     */
    public static Edge[] quickSort(Edge[] arr, int low, int high) {
        if (arr == null || arr.length == 0)
            return arr;

        if (low >= high)
            return arr;

        // pick the pivot
        int middle = low + (high - low) / 2;
        Edge pivot = arr[middle];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while (arr[i].wgt < pivot.wgt) {
                i++;
            }

            while (arr[j].wgt > pivot.wgt) {
                j--;
            }

            if (i <= j) {
                Edge temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j)
            quickSort(arr, low, j);

        if (high > i)
            quickSort(arr, i, high);

        return arr;
    }

    /**********************************************************
     *
     *       Kruskal's minimum spanning tree algorithm
     *
     **********************************************************/
    public Edge[] MST_Kruskal()
    {
        int ei, i = 0;
        int uSet, vSet;
        UnionFindSets partition = new UnionFindSets(V);
        int wgt = 0;

        // create edge array to store MST
        // Initially it has no edges.
        Edge[] mst = new Edge[V+1];

        // Not using a heap
        /* Firstly, we must sort all the edges in the array in increasing order */
        edge = quickSort(edge, 1, edge.length - 1);

        // After quick sort
        for(int j=1; j<V+1; j++) {
            uSet = edge[j].v;
            vSet = edge[j].u;
            if (partition.findSet(vSet) != partition.findSet(uSet)) {
                mst[j] = edge[j];
                partition.union(uSet, vSet);
                wgt = wgt + edge[j].wgt;
            }
        }
        System.out.println("\nWeight of MST:\n"+wgt);
        showMSTKruskal(mst);
        return mst;
    }

    public void showMSTKruskal(Edge[] mst)
    {
        System.out.print("\nMinimum spanning tree build from following edges:\n");
        for(int e = 1; e < V-1; ++e) {
            mst[e].show();
        }
        System.out.println();
    }

}

public class MST {

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("-- The following will implement Kruskal and Prim's Algorithm --");
        System.out.println("\nPlease enter the name of the text file you wish to create a graph from:");
        String graphFile = keyboard.nextLine();
        System.out.println("\nPlease enter a starting vertex");
        int startingVertex = keyboard.nextInt();


        try {
            Graph g = new Graph(graphFile);
            g.display();

            System.out.println("\n-- Prim's MST --");
            g.MST_Prim(startingVertex);

            System.out.println("\n-- Kruskal's MST --");
            g.MST_Kruskal();
        }
        catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
