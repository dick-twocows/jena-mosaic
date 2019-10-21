package org.twocows.jena.mosaic.page;

import java.nio.ByteBuffer;

import org.twocows.jena.mosaic.page.util.SystemUtil;

public class Page {

	public static final String PROPERTY_KEY = Page.class.getName() + "#PAGE_CAPACITY_DEFAULT";
	
	public static final int PAGE_CAPACITY_DEFAULT = SystemUtil.instance().getInteger(PROPERTY_KEY, 8192);
	
	/*
	 * Instance.
	 */
	
	protected ByteBuffer buffer;

	public Page() {
		this(PAGE_CAPACITY_DEFAULT);
	}
	
	public Page(final int capacity) {
		this(ByteBuffer.allocate(capacity));
	}
	
	public Page(final ByteBuffer buffer) {
		super();
		this.buffer = buffer;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public String toString() {
		return getBuffer().toString();
	}
}
