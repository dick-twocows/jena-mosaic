package org.twocows.jena.mosaic.core;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Quad;

public interface Add {
	
	void addGraph(Node graphName, Graph graph);

	void add(Quad quad);

	void add(Node g, Node s, Node p, Node o);
}
