package rk.commons.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class UriHelper {
	
	public static URI tryNewURI(String uri) {
		if (uri == null) {
			return null;
		}
		
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static String getPath(URI uri) {
		ObjectHelper.assertNotNull(uri, "uri");
		
		String path = uri.getPath();
		
		if (path == null) {
			path = uri.getSchemeSpecificPart();
			
			if (path != null) {
				int p = path.indexOf('?');
				
				if (p >= 0) {
					path = path.substring(0, p);
				}
			}
		}
		
		if (path != null) {
			boolean absolute = false;
			
			path.replace("//", "/");
			
			while (path.startsWith("/")) {
				path = path.substring(1);
				
				absolute = true;
			}
			
			if (absolute) {
				path = "/".concat(path);
			}
		}
		
		return path;
	}

	public static String getQuery(URI uri) {
		ObjectHelper.assertNotNull(uri, "uri");
		
		String query = uri.getQuery();
		
		if (query == null) {
			query = uri.getSchemeSpecificPart();
			
			if (query != null) {
				int p = query.indexOf('?');
				
				if (p >= 0) {
					query = query.substring(p + 1);
				} else {
					query = null;
				}
			}
		}
		
		return query;
	}

    public static List<Map.Entry<String, Object>> parseQuery(URI uri) {
    	String query = getQuery(uri);
    	if (query == null) {
    		return Collections.emptyList();
    	}
    	
    	List<Map.Entry<String, Object>> results = new ArrayList<Map.Entry<String, Object>>();
    	
        try {
            String[] parameters = query.split("&");
            
            for (String parameter : parameters) {
                int p = parameter.indexOf("=");
                
                if (p >= 0) {
                    String name = URLDecoder.decode(parameter.substring(0, p), "UTF-8");
                    String value = URLDecoder.decode(parameter.substring(p + 1), "UTF-8");
                    
                    results.add(new AbstractMap.SimpleEntry<String, Object>(name, value));
                } else {
                	results.add(new AbstractMap.SimpleEntry<String, Object>(parameter, null));
                }
            }
        } catch (UnsupportedEncodingException e) {
        	throw new RuntimeException("unsupported encoding: UTF-8", e);
        }
        
        return results;
    }
}
