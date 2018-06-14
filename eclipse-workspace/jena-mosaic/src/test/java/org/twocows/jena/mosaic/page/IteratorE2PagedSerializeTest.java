package org.twocows.jena.mosaic.page;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class IteratorE2PagedSerializeTest {

	@Test
	public void test() {
		final List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		
		final IteratorE2PagedSerialize<Integer> iteratorE2PagedSerialize = new IteratorE2PagedSerialize<>(list.iterator());
		
		while (iteratorE2PagedSerialize.hasNext()) {
			Page page = (Page) iteratorE2PagedSerialize.next();
			System.out.println(page);
		}
	}

}
