package org.twocows.jena.mosaic.core;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Quad;

public class AddSimple implements Add {

	private final Tessera tessera;
	
	public AddSimple(final Tessera tessera) {
		super();
		this.tessera = tessera;
	}

	@Override
	public void addGraph(Node graphName, Graph graph) {
		this.tessera.datasetGraph().addGraph(graphName, graph);
	}

	@Override
	public void add(Quad quad) {
		this.tessera.datasetGraph().add(quad);
	}

	@Override
	public void add(Node g, Node s, Node p, Node o) {
		this.tessera.datasetGraph().add(g, s, p, o);
	}

}
