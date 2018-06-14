package org.twocows.jena.mosaic.page.rdf;

import org.apache.jena.sparql.core.Quad;
import org.twocows.jena.mosaic.page.InputStreamPaged;

public class IteratorPaged2RDFQuad extends IteratorPaged2RDF<Quad> {

	public IteratorPaged2RDFQuad(final InputStreamPaged inputStreamPaged) {
		super(inputStreamPaged);
	}

	@Override
	protected void quad(final Quad quad) {
		hasNext = true;
		next = quad;
	}
}
