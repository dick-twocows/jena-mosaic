package org.twocows.jena.mosaic.core;

import org.apache.jena.sparql.core.DatasetGraph;

public class Tessera {

	private final DatasetGraph datasetGraph;
	
	public Tessera(final DatasetGraph datasetGraph) {
		super();
		this.datasetGraph = datasetGraph;
	}
	
	public DatasetGraph datasetGraph() {
		return this.datasetGraph;
	}

	@Override
	public String toString() {
		return datasetGraph().toString();
	}
}
