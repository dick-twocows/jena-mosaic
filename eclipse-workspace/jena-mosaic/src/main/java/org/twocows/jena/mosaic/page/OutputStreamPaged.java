package org.twocows.jena.mosaic.page;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class OutputStreamPaged extends OutputStream {

	protected int pageCount = 0;
	
	protected int byteCount = 0;
	
	protected Deque<Page> pages = new ConcurrentLinkedDeque<>();
	
	protected Page create() {
		pageCount++;
		return new Page();
	}

	protected Deque<Page> pages() {
		return pages;
	}
	
	public Page writePage() {
		Page page = pages.peekLast();

		if (page == null || (!page.getBuffer().hasRemaining())) {
			page = create();
			pages.addLast(page);
		}
		
		return page;
	}
			
	/*
	 * OutputStream
	 */
	
	@Override
	public void write(int b) throws IOException {
		writePage().getBuffer().put((byte) b);
		byteCount = byteCount + 1;
	}

	@Override
	public void write(byte[] b, int offset, int length) throws IOException {
		int o = offset;
		int l = length;
		Page page;
		int c;
		while (l > 0) {
			page = writePage();
			c = (l <= page.getBuffer().remaining() ? l : page.getBuffer().remaining());
			page.getBuffer().put(b, o, c);
			o = o + c;
			l = l - c;
		}
		byteCount = byteCount + length;
	}

	@Override
	public String toString() {
		return byteCount + " " + pageCount + " " + pages.toString();
	}
}
