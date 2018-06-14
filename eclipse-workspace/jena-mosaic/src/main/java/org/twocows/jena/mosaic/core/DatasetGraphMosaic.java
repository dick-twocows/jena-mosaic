package org.twocows.jena.mosaic.core;

import static java.lang.ThreadLocal.withInitial;
import static org.apache.jena.query.ReadWrite.WRITE;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.TxnType;
import org.apache.jena.sparql.JenaTransactionException;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.core.Transactional.Promote;
import org.apache.jena.sparql.core.mem.DatasetGraphInMemory;

import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;

/**
 * @author DickM
 * 
 * A DatasetGraph (mosaic) which is composed of multiple (tesserae) DatasetGraph's (tessera).
 * 
 * A begin() will always return true! This is because tessera transactions are optimistic. 
 *
 */
public class DatasetGraphMosaic implements DatasetGraph {

	/*
	 * Static
	 */
	
	public static DatasetGraphMosaic simple() {
		final Tessera tessera = new Tessera(new DatasetGraphInMemory());
		
		final Add add = new AddSimple(tessera);
		
		final DatasetGraphMosaic datasetGraphMosaic = new DatasetGraphMosaic();
		datasetGraphMosaic.tesserae().add(tessera);
		datasetGraphMosaic.add(add);
		
		return datasetGraphMosaic;
	}
	
	/*
	 * Instance
	 */
	
	/**
	 * The tessera making up this mosaic.
	 */
	private volatile Set<Tessera> tesserae = new ConcurrentSkipListSet<>();
	
	/**
	 * How to add.
	 */
	private volatile Add add = null;
	
	/**
	 * How to delete.
	 */
	private volatile Delete delete = null;
	
    private final ThreadLocal<Boolean> isInTransaction = withInitial(() -> false);

    private final ThreadLocal<TxnType> transactionType = withInitial(() -> null);

    private final ThreadLocal<ReadWrite> transactionMode = withInitial(() -> null);

    /*
     * Mosaic
     */
    
    public DatasetGraphMosaic() {
		super();
	}
    
    public Set<Tessera> tesserae() {
    	return this.tesserae;
    }
    
    protected Stream<Tessera> sequential() {
    	return tesserae
    		.stream();
    }

	protected Stream<Tessera> parallel() {
    	return tesserae
    		.parallelStream();
    }

    /*
     * Mosaic add
     */
    
	public DatasetGraphMosaic add(final Add add) {
		this.add = add;
		return this;
	}
	
    public Add add() {
    	return this.add;
    }
    
    /*
     * Mosaic delete
     */
    
    public DatasetGraphMosaic delete(final Delete delete) {
    	this.delete = delete;
    	return this;
    }
    
    public Delete delete() {
    	return this.delete;
    }
    
    /*
     * Mosaic transaction
     */
    
    protected DatasetGraph begin(final DatasetGraph datasetGraph) {
    	return begin(datasetGraph, transactionType.get());
    }
    
    protected DatasetGraph begin(final DatasetGraph datasetGraph, final ReadWrite readWrite) {
    	if (!datasetGraph.isInTransaction()) {
    		datasetGraph.begin(readWrite);
    	}
    	return datasetGraph;
    }
    
    protected DatasetGraph begin(final DatasetGraph datasetGraph, final TxnType txnType) {
    	if (!datasetGraph.isInTransaction()) {
    		datasetGraph.begin(txnType);
    	}
    	return datasetGraph;
    }
    
    protected DatasetGraph promote(final DatasetGraph datasetGraph, final Promote promote) {
    	datasetGraph.promote(promote);
    	return datasetGraph;
    }
    
    protected DatasetGraph commit(final DatasetGraph datasetGraph) {
    	datasetGraph.commit();
    	return datasetGraph;
    }
    
    protected DatasetGraph abort(final DatasetGraph datasetGraph) {
    	datasetGraph.abort();
    	return datasetGraph;
    }
    
    protected DatasetGraph end(final DatasetGraph datasetGraph) {
    	datasetGraph.end();
    	return datasetGraph;
    }
    
    /*
     * DatasetGraph
     */
    
	@Override
	public void close() {
        if (isInTransaction()) {
            abort();
        }
        parallel()
        	.forEach(
        		(tessera) -> {
        			tessera.datasetGraph().close();
        		}
        	);
	}
	
	/*
	 * datasetGraph add...
	 */
	
	@Override
	public void addGraph(Node graphName, Graph graph) {
		Objects.requireNonNull(add()).addGraph(graphName, graph);
	}

	@Override
	public void add(Quad quad) {
		Objects.requireNonNull(add()).add(quad);
	}

	@Override
	public void add(Node g, Node s, Node p, Node o) {
		Objects.requireNonNull(add()).add(g, s, p, o);
	}

	/*
	 * DatasetGraph delete.
	 */

	@Override
	public void delete(Quad quad) {
		Objects.requireNonNull(delete()).delete(quad);
	}

	@Override
	public void delete(Node g, Node s, Node p, Node o) {
		Objects.requireNonNull(delete()).delete(g, s, p, o);
	}

	@Override
	public void deleteAny(Node g, Node s, Node p, Node o) {
		Objects.requireNonNull(delete()).deleteAny(g, s, p, o);
	}

	@Override
	public void clear() {
		Objects.requireNonNull(delete()).clear();
	}
	
	/*
	 * DatasetGraph contains...
	 */
	
	@Override
	public boolean containsGraph(Node graphNode) {
		return parallel()
			.map(
				(tessera) -> {
					return begin(tessera.datasetGraph()).containsGraph(graphNode);
				}
			)
			.filter((contains) -> {return contains;})
			.findFirst()
			.orElse(false);
	}

	@Override
	public boolean contains(Node g, Node s, Node p, Node o) {
		return parallel()
			.map(
				(tessera) -> {
					return begin(tessera.datasetGraph()).contains(g, s, p, o);
				}
			)
			.filter((contains) -> {return contains;})
			.findFirst()
			.orElse(false);
	}

	@Override
	public boolean contains(Quad quad) {
		return parallel()
			.map(
				(tessera) -> {
					return begin(tessera.datasetGraph()).contains(quad);
				}
			)
			.filter((contains) -> {return contains;})
			.findFirst()
			.orElse(false);
	}
	
	/*
	 * DatasetGraph find...
	 */

	@Override
	public Iterator<Quad> find() {
		return parallel()
			.flatMap(
				(tessera) -> {
					return
						StreamSupport.stream(
							Spliterators.spliteratorUnknownSize(
								begin(tessera.datasetGraph()).find(),
								Spliterator.NONNULL
							),
							false
						);
				}
			)
			.distinct()
			.iterator();
	}

	@Override
	public Iterator<Quad> find(Quad quad) {
		return parallel()
			.flatMap(
				(tessera) -> {
					return
						StreamSupport.stream(
							Spliterators.spliteratorUnknownSize(
								begin(tessera.datasetGraph()).find(quad),
								Spliterator.NONNULL
							),
							false
						);
				}
			)
			.distinct()
			.iterator();
	}

	@Override
	public Iterator<Quad> find(Node g, Node s, Node p, Node o) {
		return parallel()
			.flatMap(
				(tessera) -> {
					return
						StreamSupport.stream(
							Spliterators.spliteratorUnknownSize(
								begin(tessera.datasetGraph()).find(g, s, p, o),
								Spliterator.NONNULL
							),
							false
						);
				}
			)
			.distinct()
			.iterator();
	}

	@Override
	public Iterator<Quad> findNG(Node g, Node s, Node p, Node o) {
		return parallel()
			.flatMap(
				(tessera) -> {
					return
						StreamSupport.stream(
							Spliterators.spliteratorUnknownSize(
								begin(tessera.datasetGraph()).find(g, s, p, o),
								Spliterator.NONNULL
							),
							false
						);
				}
			)
			.distinct()
			.iterator();
	}
	
	/*
	 * Transactional
	 */

	@Override
	public void begin(final TxnType txnType) {
        if (isInTransaction()) { 
            throw new JenaTransactionException("Transactions cannot be nested!");
        }
        transactionType.set(txnType);
        transactionMode.set(TxnType.initial(txnType));
        isInTransaction.set(true);
	}

	@Override
	public void begin(final ReadWrite readWrite) {
		begin(TxnType.convert(readWrite));
	}

	@Override
	public boolean promote(final Promote promoteMode) {
        if (!isInTransaction()) {
        	throw new JenaTransactionException("Tried to promote outside a transaction!");
        }
        return false ;
	}

	@Override
	public void commit() {
        if (!isInTransaction()) {
        	throw new JenaTransactionException("Tried to commit outside a transaction!");
        }
        parallel()
        	.forEach(
	        	(tessera) -> {
	        		tessera.datasetGraph().commit();
	        	}
	        );
        transactionType.remove();
        transactionMode.remove();
        isInTransaction.remove();
	}

	@Override
	public void abort() {
        if (!isInTransaction()) { 
        	throw new JenaTransactionException("Tried to abort outside a transaction!");
        }
        parallel()
    	.forEach(
        	(tessera) -> {
        		tessera.datasetGraph().abort();
        	}
        );
        transactionType.remove();
        transactionMode.remove();
        isInTransaction.remove();
	}

	@Override
	public void end() {
        if (isInTransaction()) {
        	final String text = String.format("Called end() before abot() or commit() in [%s] [%s]", transactionMode.get(), transactionType.get()); 
        	abort() ;
            parallel()
	        	.forEach(
		        	(tessera) -> {
		        		tessera.datasetGraph().end();
		        	}
		        );
        	// This might annoy users.!?
            throw new JenaTransactionException(text);
        }
	}

	@Override
	public ReadWrite transactionMode() {
		return transactionMode.get();
	}

	@Override
	public TxnType transactionType() {
		return transactionType.get();
	}

	@Override
	public boolean isInTransaction() {
		return isInTransaction.get();
	}

}
