package org.xmlcml.xhtml2stm.visitable.xml;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.visitable.AbstractVisitable;
import org.xmlcml.xml.XMLUtil;

public class XMLVisitable extends AbstractVisitable  {

	private final static Logger LOG = Logger.getLogger(XMLVisitable.class);

	private static final String XML = "xml";
	public static final String ITALIC= ".//*[local-name()='italic' or local-name()='i' or local-name()='it']";

	private List<XMLContainer> xmlContainerList;

	public XMLVisitable() {
		
	}

	@Override
	public void addFile(File xmlFile) throws Exception {
		ensureXMLContainerList();
		ensureFileList();
		try {
			this.fileList.add(xmlFile);
//			Document document = XMLUtil.parseQuietlyToDocumentWithoutDTD(xmlFile);
			Element root = XMLUtil.stripDTDAndParse(FileUtils.readFileToString(xmlFile));
			XMLContainer xmlContainer = new XMLContainer(xmlFile, root); 
			xmlContainerList.add(xmlContainer);
		} catch (Throwable t) {
			LOG.error("Cannot parse document: "+xmlFile+" ("+t+") ");
		}
	}

	@Override
	public void addURL(URL url) throws Exception {
		ensureXMLContainerList();
		try {
			Document document = new Builder().build(url.openStream());
			XMLContainer xmlContainer = new XMLContainer(url, document.getRootElement()); 
			xmlContainerList.add(xmlContainer);
		} catch (Throwable t) {
			LOG.error("Cannot parse document: "+url+" ("+t+") ");
		}
	}

	private void ensureXMLContainerList() {
		if (xmlContainerList == null) {
			xmlContainerList = new ArrayList<XMLContainer>();
		}
	}

	public List<XMLContainer> getXMLContainerList() {
		ensureXMLContainerList();
		if (super.findFilesInDirectories() != null) {
			xmlContainerList = createContainersFromFiles();
		} 
		return xmlContainerList;
	}

	public List<XMLContainer> createContainersFromFiles() {
		xmlContainerList = new ArrayList<XMLContainer>();
		if (fileList != null) {
			for (File file : fileList) {
				try {
//					Element xmlElement = XMLUtil.parseQuietlyToDocument(file).getRootElement();
					Element xmlElement = XMLUtil.stripDTDAndParse(FileUtils.readFileToString(file));
					xmlContainerList.add(new XMLContainer(file, xmlElement));
				} catch (Exception e) {
					LOG.error("file: "+file+"; "+file.exists());
					LOG.error("not an XML file: "+file+ ": " + e);
				}
			}
		}
		return xmlContainerList;
		
	}

	@Override
	public String[] getExtensions() {
		return new String[] {"xml"};
	}

	@Override
	public void getMetadata() {
		for (XMLContainer xmlContainer : xmlContainerList) {
			getMetadata(xmlContainer.getElement());
		}
	}

	private void getMetadata(Element xml) {
		//FIXME
	}
	
	public static boolean hasSuffix(String suffix) {
		return XML.equalsIgnoreCase(suffix);
	}
	
}
