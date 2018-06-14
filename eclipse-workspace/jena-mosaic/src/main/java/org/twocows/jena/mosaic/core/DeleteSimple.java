package org.twocows.jena.mosaic.core;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Quad;

public class DeleteSimple implements Delete {

	private final Tessera tessera;
	
	public DeleteSimple(final Tessera tessera) {
		super();
		this.tessera = tessera;
	}

	@Override
	public void delete(Quad quad) {
		this.tessera.datasetGraph().delete(quad);
	}

	@Override
	public void delete(Node g, Node s, Node p, Node o) {
		this.tessera.datasetGraph().delete(g, s, p, o);		
	}

	@Override
	public void deleteAny(Node g, Node s, Node p, Node o) {
		this.tessera.datasetGraph().deleteAny(g, s, p, o);
	}

	@Override
	public void clear() {
		this.tessera.datasetGraph().clear();
	}

}
