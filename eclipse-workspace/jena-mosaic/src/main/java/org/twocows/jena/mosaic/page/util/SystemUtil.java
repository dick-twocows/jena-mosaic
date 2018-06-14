package org.twocows.jena.mosaic.page.util;

public class SystemUtil {

	private static final SystemUtil instance = new SystemUtil();
	
	public static SystemUtil instance() {
		return instance;
	}
	
	private SystemUtil() {
	}
	
	public Integer getInteger(final String key, final Integer value) {
		Integer result = value;
		String defined = System.getProperty(key);
		if (defined != null) {
			try {
				result = Integer.valueOf(value);
			} catch (final NumberFormatException numberFormatException) {
				System.err.println(String.format("Failed to convert [%s] to int", value));
			}
		}
		return result;
	}
}
