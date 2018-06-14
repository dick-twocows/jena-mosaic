package org.twocows.jena.mosaic.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThreadProxyTest {

	@Test
	public void test() {
		final ThreadProxy threadProxy = new ThreadProxy();
		threadProxy.submit(new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello World");
			}
		});
		threadProxy.close();
	}

}
