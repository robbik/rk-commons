package rk.commons.inject.context;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import rk.commons.inject.factory.ObjectFactory;
import rk.commons.inject.factory.ObjectNotFoundException;
import rk.commons.inject.factory.config.ObjectDefinition;
import rk.commons.inject.factory.support.ObjectDefinitionRegistry;
import rk.commons.inject.factory.xml.NamespaceHandlerResolver;
import rk.commons.inject.factory.xml.NamespaceSchemaResolver;
import rk.commons.inject.factory.xml.ObjectDefinitionParserDelegate;
import rk.commons.inject.factory.xml.XmlObjectDefinitionReader;
import rk.commons.loader.ResourceLoader;
import rk.commons.logging.Logger;
import rk.commons.logging.LoggerFactory;

public class XmlContext {

    private static final Logger log = LoggerFactory.getLogger(XmlContext.class);
    
    private ResourceLoader resourceLoader;

    private NamespaceHandlerResolver handlerResolver;

    private NamespaceSchemaResolver schemaResolver;

    protected ObjectFactory objectFactory;

    protected ObjectDefinitionRegistry objectDefinitionRegistry;
    
    protected String xmlDefaultNamespace;

    private String[] locations;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public void setNamespaceHandlerPath(String path) {
        handlerResolver = new NamespaceHandlerResolver(resourceLoader, path);
    }
    
    public void setNamespaceSchemaPath(String path) {
        schemaResolver = new NamespaceSchemaResolver(resourceLoader, path);
    }
    
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;  
    }
    
    public void setObjectDefinitionRegistry(ObjectDefinitionRegistry objectDefinitionRegistry) {
        this.objectDefinitionRegistry = objectDefinitionRegistry;
    }
    
    public void setXmlDefaultNamespace(String xmlDefaultNamespace) {
        this.xmlDefaultNamespace = xmlDefaultNamespace;
    }
    
    public void setLocations(String... locations) {
        this.locations = locations;
    }

    protected void loadObjectDefinitions(URL url, Set<URL> importedURLs) {
        if (!importedURLs.add(url)) {
            // already loaded, skip it!
            return;
        }
        
        ObjectDefinitionParserDelegate delegate = new ObjectDefinitionParserDelegate(resourceLoader);
        delegate.setObjectDefinitionRegistry(objectDefinitionRegistry);
        delegate.setNamespaceHandlerResolver(handlerResolver);
        delegate.setObjectNamePrefix(null);

        XmlObjectDefinitionReader reader = new XmlObjectDefinitionReader(url, schemaResolver);

        Element rootElement = null;

        try {
            rootElement = reader.parse();
        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                SAXParseException pex = (SAXParseException) e;
                
                throw new RuntimeException(
                        pex.getMessage() + " (" + url + ":"
                                + pex.getLineNumber() + ":"
                                + pex.getColumnNumber() + ")", e.getCause());
            } else {
                throw new RuntimeException(e.getMessage() + "(" + url + ")", e);
            }
        } catch (IOException e) {
            log.error("unable to parse " + url, e);
        }

        if (rootElement == null) {
            return;
        }

        NodeList nodes = rootElement.getChildNodes();

        for (int i = 0, n = nodes.getLength(); i < n; ++i) {
            Node node = nodes.item(i);

            if (node instanceof Element) {
                Element element = (Element) node;

                if (xmlDefaultNamespace.equals(node.getNamespaceURI())) {
                    if ("import".equals(node.getLocalName())) {
                        URL[] refURLs = resourceLoader.getURLs(element.getAttribute("url"));

                        for (int j = 0, m = refURLs.length; j < m; ++j) {
                            loadObjectDefinitions(refURLs[j], importedURLs);
                        }
                    } else if ("require".equals(node.getLocalName())) {
                        URL[] refURLs = resourceLoader.getURLs(element.getAttribute("url"));

                        if (refURLs.length == 0) {
                            throw new RuntimeException("no required resources " +
                                    element.getAttribute("url") + " by " + url);
                        }

                        for (int j = 0, m = refURLs.length; j < m; ++j) {
                            loadObjectDefinitions(refURLs[j], importedURLs);
                        }
                    } else {
                        delegate.parse((Element) node);
                    }
                } else {
                    delegate.parse((Element) node);
                }
            }
        }
    }

    public Set<URL> refresh(boolean lazy) {
        Set<URL> importedURLs = new HashSet<URL>();
        
        for (int i = 0, n = locations.length; i < n; ++i) {
            URL[] docURLs = resourceLoader.getURLs(locations[i]);
            
            for (int j = 0; j < docURLs.length; ++j) {
                loadObjectDefinitions(docURLs[j], importedURLs);
            }
        }

        if (!lazy) {
            Set<String> objectQNames = objectDefinitionRegistry.getObjectNames();
    
            for (String objectQName : objectQNames) {
                ObjectDefinition def;
                
                try {
                    def = objectDefinitionRegistry.getObjectDefinition(objectQName);
                } catch (ObjectNotFoundException e) {
                    continue;
                }
                
                objectFactory.createObject(def);
            }
        }

        return importedURLs;
    }

    public URL refresh(String location) {
        URL docURL = resourceLoader.getURL(location);

        if (docURL != null) {
            loadObjectDefinitions(docURL, new HashSet<URL>());
        }

        return docURL;
    }
}
