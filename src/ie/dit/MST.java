package ie.dit;
/**
 * MST Algorithms Assignment
 *
 * This is the MST Algorithms Assignment for Algorithms and Data Structures. It implements
 * Prim's and Kruskal's Algorithms.
 *
 * Author: Mark Barrett
 * Version: 1.0
 * Date: 27/03/2017
 */
import java.io.*;
import java.util.Scanner;

//Heap Class for Prim's Algorithm
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

        // create sentinel node
        z = new Node();
        z.next = z;

        // create adjacency lists, initialised to sentinel node z
        adj = new Node[V+1];
        for(v = 1; v <= V; ++v)
            adj[v] = z;

        // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
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
            if(adj[v] == z) {
                adj[v] = m;
                m.next = z;
            }
            else {
                m.next = adj[v].next;
                adj[v].next = m;
            }

            // Add the connection to the right hand vertices list
            if(adj[u] == z) {
                adj[u] = n;
                n.next = z;
            }
            //If it doesn't point the sentinel
            else {
                //Attach to the first edge
                n.next = adj[u].next;
                adj[u].next = n;
            }
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

        System.out.println("\nPrinting out the adjaceny matrix of lists");
        for(v=1; v<=V; ++v){
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + n.vert + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }



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
            g.MST_Prim(startingVertex);
        }
        catch(Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
