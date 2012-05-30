package rk.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class UriHelperTest {

	@Test(expected = NullPointerException.class)
	public void testGetQueryIfOnlySchemeSpecified() {
		UriHelper.getQuery(UriHelper.tryNewURI("edc:"));
	}

	@Test
	public void testGetQueryIfNoPathSpecified() {
		assertEquals("a=2", UriHelper.getQuery(UriHelper.tryNewURI("edc:?a=2")));
	}

	@Test
	public void testGetQueryIfSlashFound() {
		assertEquals("a=3", UriHelper.getQuery(UriHelper.tryNewURI("edc://SIGN_ON/x?a=3")));
	}

	@Test
	public void testGetQueryIfNoSlashFound() {
		assertEquals("a=3", UriHelper.getQuery(UriHelper.tryNewURI("edc:SIGN_ON?a=3")));
	}

	@Test
	public void testGetQueryIfNoQuerySpecified() {
		assertNull(UriHelper.getQuery(UriHelper.tryNewURI("edc:SIGN_ON")));
	}

	@Test(expected = NullPointerException.class)
	public void testGetQueryIfUriIsNull() {
		UriHelper.getQuery(null);
	}

	@Test
	public void testParseQueryIfNoQuerySpecified() {
		assertEquals(0, UriHelper.parseQuery(UriHelper.tryNewURI("edc:SIGN_ON")).size());
	}

	@Test
	public void testParseQueryIfOneQuerySpecified() {
		List<Map.Entry<String, Object>> params = UriHelper.parseQuery(UriHelper.tryNewURI("edc:SIGN_ON?x=3"));
		
		assertEquals(1, params.size());
		
		assertEquals("x", params.get(0).getKey());
		assertEquals("3", params.get(0).getValue());
	}

	@Test
	public void testParseQueryIfTwoQuerySpecified() {
		List<Map.Entry<String, Object>> params = UriHelper.parseQuery(UriHelper.tryNewURI("edc:SIGN_ON?x=3&y=7"));
		
		assertEquals(2, params.size());
		
		assertEquals("x", params.get(0).getKey());
		assertEquals("3", params.get(0).getValue());
		
		assertEquals("y", params.get(1).getKey());
		assertEquals("7", params.get(1).getValue());
	}
	
	@Test
	public void testGetPathIfPathIsRelativeAndNoQuerySpecified() {
		assertEquals("SIGN_ON", UriHelper.getPath(UriHelper.tryNewURI("edc:SIGN_ON")));
	}
	
	@Test
	public void testGetPathIfPathIsRelativeAndQuerySpecified() {
		assertEquals("SIGN_ON", UriHelper.getPath(UriHelper.tryNewURI("edc:SIGN_ON?a=3")));
	}
	
	@Test
	public void testGetPathIfPathIsAbsoluteAndNoQuerySpecified() {
		assertEquals("/SIGN_ON", UriHelper.getPath(UriHelper.tryNewURI("edc:/SIGN_ON")));
	}
	
	@Test
	public void testGetPathIfPathIsAbsoluteWithDoubleSlashAndNoQuerySpecified() {
		assertEquals("/SIGN_ON", UriHelper.getPath(UriHelper.tryNewURI("edc://aa/SIGN_ON")));
	}
}
