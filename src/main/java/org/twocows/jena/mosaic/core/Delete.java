package org.twocows.jena.mosaic.core;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Quad;

public interface Delete {

	void delete(Quad quad);
	
	void delete(Node g, Node s, Node p, Node o);
	
	void deleteAny(Node g, Node s, Node p, Node o);

	void clear();
}
