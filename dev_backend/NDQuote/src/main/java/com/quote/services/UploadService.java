package com.quote.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.quote.vm.MessageVM;
import com.quote.vm.NFEInfoVM;
import com.quote.x509keyselector.X509KeySelector;


@Service
public class UploadService {


	public MessageVM upload( MultipartFile file) throws Exception {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("xml") ) {
			return uploadXml(file);
		} else if (extension.equalsIgnoreCase("zip")) {
			return uploadZip(file);
		} else {
			throw new Exception("Please upload .xml or .zip file which contain .xml files.");
		}
		
	}

	public MessageVM uploadZip( MultipartFile file) {
		MessageVM messageVM = new MessageVM();
		List<NFEInfoVM> nfeInfoList = new ArrayList<>();
		try {
			ZipInputStream zis = new ZipInputStream(file.getInputStream());
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null)
			{
				if (entry.getSize() <= 0) {
					continue;
				}
				String extension = FilenameUtils.getExtension(entry.getName());
				if ( extension.equalsIgnoreCase("xml") ) {
					try {
						throw new Exception("Zip file should contained only .xml files.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("entry.getName() :: " + entry.getName());
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream((int) entry.getSize());
				for (int c = zis.read(); c != -1; c = zis.read()) {
					arrayOutputStream.write(c);
				}

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc =  dBuilder.parse(new InputSource(new ByteArrayInputStream(arrayOutputStream.toString().getBytes("utf-8"))));
				doc.getDocumentElement().normalize();
				NodeList nFENodeList = doc.getElementsByTagName("NFe");
				if ( nFENodeList != null && nFENodeList.item(0) != null ) {
					Node nNode = nFENodeList.item(0);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
					}		

				}

				NodeList nList = doc.getElementsByTagName("infNFe");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						NFEInfoVM infoVM =new NFEInfoVM(); 
						String InfNFeId = eElement.getAttribute("Id");
						infoVM.setInfNFeId(InfNFeId.replaceAll("NFe", ""));
						infoVM.setnNF(Integer.parseInt(eElement.getElementsByTagName("nNF").item(0).getTextContent()));
						infoVM.setxNome(eElement.getElementsByTagName("xNome").item(0).getTextContent());
						if(eElement.getElementsByTagName("dEmi").item(0) != null) {
							infoVM.setdEmi(eElement.getElementsByTagName("dEmi").item(0).getTextContent());	
						}

						if (isValidSignature(arrayOutputStream)) {
							infoVM.setStatus("Valid");
						} else {
							infoVM.setStatus("Invalid");
						}

						nfeInfoList.add(infoVM);
					}
				}
				arrayOutputStream.close();
				zis.closeEntry();
			}
			messageVM.setCode("200");
			messageVM.setMessage("All xml files results.");;
		} catch (SAXException ex ) {
			ex.printStackTrace();
			SAXParseException spe = new SAXParseException(ex.getMessage(), null, ex);
			System.err.println(this.getClass()+ "@@@@@@@---> Parsing error at line = " + spe.getLineNumber() + " and column=" + spe.getColumnNumber());
			messageVM.setCode("500");
			messageVM.setMessage("Parsing error at line = " + spe.getLineNumber() + " and column=" + spe.getColumnNumber());
		}catch ( ParserConfigurationException e) {
			messageVM.setCode("500");
			messageVM.setMessage("Someting wents to wrong.");
			e.printStackTrace();
		}catch (IOException  e) {
			messageVM.setCode("500");
			messageVM.setMessage("Someting wents to wrong.");
			e.printStackTrace();
		}
		messageVM.setData(nfeInfoList);
		return messageVM;
	}


	public MessageVM uploadXml( MultipartFile file) {
		MessageVM messageVM = new MessageVM();
		List<NFEInfoVM> nfeInfoList = new ArrayList<>();
		try {
			ByteArrayInputStream arrayInputStream = null;
			try {
				arrayInputStream = new ByteArrayInputStream(file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream((int) file.getSize());
			for (int c = arrayInputStream.read(); c != -1; c = arrayInputStream.read()) {
				arrayOutputStream.write(c);
			} 

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc =  dBuilder.parse(new InputSource(new ByteArrayInputStream(arrayOutputStream.toString().getBytes("utf-8"))));
			doc.getDocumentElement().normalize();
			NodeList nFENodeList = doc.getElementsByTagName("NFe");
			if ( nFENodeList != null && nFENodeList.item(0) != null ) {
				Node nNode = nFENodeList.item(0);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
				}		

			}

			NodeList nList = doc.getElementsByTagName("infNFe");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					NFEInfoVM infoVM =new NFEInfoVM(); 
					String InfNFeId = eElement.getAttribute("Id");
					infoVM.setInfNFeId(InfNFeId.replaceAll("NFe", ""));
					infoVM.setnNF(Integer.parseInt(eElement.getElementsByTagName("nNF").item(0).getTextContent()));
					infoVM.setxNome(eElement.getElementsByTagName("xNome").item(0).getTextContent());
					if(eElement.getElementsByTagName("dEmi").item(0) != null) {
						infoVM.setdEmi(eElement.getElementsByTagName("dEmi").item(0).getTextContent());	
					}
					if (isValidSignature(arrayOutputStream)) {
						infoVM.setStatus("Valid");
					} else {
						infoVM.setStatus("Invalid");
					}

					nfeInfoList.add(infoVM);
				}
			}
			arrayOutputStream.close();
			arrayInputStream.close();
			messageVM.setCode("200");
			messageVM.setMessage("Xml files results.");
		} catch (SAXException ex ) {
			ex.printStackTrace();
			SAXParseException spe = new SAXParseException(ex.getMessage(), null, ex);
			messageVM.setCode("500");
			messageVM.setMessage("Parsing error at line = " + spe.getLineNumber() + " and column=" + spe.getColumnNumber());
		} catch ( ParserConfigurationException e) {
			messageVM.setCode("500");
			messageVM.setMessage("Someting wents to wrong.");
			e.printStackTrace();
		} catch (IOException  e) {
			messageVM.setCode("500");
			messageVM.setMessage("Someting wents to wrong.");
			e.printStackTrace();
		}
		messageVM.setData(nfeInfoList);
		return messageVM;
	}


	//is validate signature 
	public static boolean isValidSignature (ByteArrayOutputStream arrayOutputStream) {
		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = dbf.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(arrayOutputStream.toString().getBytes("utf-8"))));

			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(0));
			NodeList nList = doc.getElementsByTagName("infNFe");
			valContext.setIdAttributeNS((Element) nList.item(0), null, "Id");

			try {
				XMLSignature signature = fac.unmarshalXMLSignature(valContext);
				if ( signature.validate(valContext) ) {
					return true;
				} else {
					return false;
				}

			} catch (MarshalException e) {
				e.printStackTrace();
				return false;
			} catch (XMLSignatureException e) {
				e.printStackTrace();
				return false;
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	public MessageVM checkValidSignature( MultipartFile file) {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("xml") ) {
			ByteArrayInputStream arrayInputStream = null;
			try {
				arrayInputStream = new ByteArrayInputStream(file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream((int) file.getSize());
			for (int c = arrayInputStream.read(); c != -1; c = arrayInputStream.read()) {
				arrayOutputStream.write(c);
			}

			System.out.println("arrayOutputStream ::: " + arrayOutputStream);
			try {

				XMLSignatureFactory fac = XMLSignatureFactory
						.getInstance("DOM");

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				Document doc = dbf.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(arrayOutputStream.toString().getBytes("utf-8"))));

				doc.getDocumentElement().normalize();
				NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
				DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(0));
				NodeList nList = doc.getElementsByTagName("infNFe");
				valContext.setIdAttributeNS((Element) nList.item(0), null, "Id");
				try {
					XMLSignature signature = fac.unmarshalXMLSignature(valContext);

					// Validate the XMLSignature.
					System.out.println("validate :: " +  signature.validate(valContext));


				} catch (MarshalException e) {
					System.out.println("validate :: MarshalException" );
					e.printStackTrace();
				} catch (XMLSignatureException e) {
					System.out.println("validate :: XMLSignatureException" );
					e.printStackTrace();
				}

			} catch (ParserConfigurationException e) {
				System.out.println("validate :: ParserConfigurationException" );
				e.printStackTrace();
			} catch (SAXException e) {
				System.out.println("validate :: SAXException" );
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("validate :: Exception" );
				e.printStackTrace();
			}
		} else {

		}
		return null;
	}
}