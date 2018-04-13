package com.quote.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.quote.vm.MessageVM;
import com.quote.x509keyselector.X509KeySelector;

public class ValidationData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ValidationData vd = new ValidationData();
		//String filePath ="D:\\dev\\workspace\\NDQuote\\Assets\\COMPRA\\35140861936522000194550010002002631257312270-nfe.xml" ;
		String filePath ="D:\\dev\\workspace\\NDQuote\\Assets\\COMPRA\\35160346340832000158550010001109071006765602-procNFe.xml" ;
		//String filePath ="D:\\dev\\workspace\\NDQuote\\Assets\\COMPRA\\35170444106466000141550050002187941002430911-nfe.xml" ;
		File file = new File(filePath);
		try {
			vd.checkValidSignature(file,filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MessageVM checkValidSignature( File file,String filePath) throws IOException {
		System.out.println("File ::"+ file.getName());
		String extension = FilenameUtils.getExtension(file.getName());
		System.out.println("File extension ::"+ extension);
		if (extension.equalsIgnoreCase("xml") ) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream((int) file.length());
			for (int c = fileInputStream.read(); c != -1; c = fileInputStream.read()) {
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
