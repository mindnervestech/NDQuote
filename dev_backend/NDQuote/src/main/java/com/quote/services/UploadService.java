package com.quote.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.quote.entities.Tokens;
import com.quote.entities.XMLDocument;
import com.quote.entities.auth.AuthUser;
import com.quote.repository.AuthUserRepository;
import com.quote.repository.TokenRepository;
import com.quote.repository.XMLDocumentRepository;
import com.quote.utility.SendEmail;
import com.quote.vm.EmailVM;
import com.quote.vm.MessageVM;
import com.quote.vm.NFEInfoVM;
import com.quote.x509keyselector.X509KeySelector;


@Service
public class UploadService {

	@Autowired
	private SendEmail sendEmail;
	
	@Autowired
	AuthUserRepository authUserRepository;
	
	@Autowired
	XMLDocumentRepository xmlDocumentRepository;
	@Autowired
	TokenRepository tokenRepository;

	
	public MessageVM upload(Long userId, MultipartFile file) throws Exception {
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("xml") ) {
			return uploadXml(userId,file);
		} else if (extension.equalsIgnoreCase("zip")) {
			return uploadZip(userId,file);
		} else {
			throw new Exception("Please upload .xml or .zip file which contain .xml files.");
		}
		
	}

	public MessageVM uploadZip( Long userId, MultipartFile file) {
		AuthUser user = authUserRepository.findOne(userId);
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
						XMLDocument xmlDocument = new XMLDocument(); 
						xmlDocument.setUser(user);
						xmlDocument.setCreatedAt(new Date());
						xmlDocument.setUpdatedAt(new Date());
						NFEInfoVM infoVM =new NFEInfoVM(); 
						Tokens token = new Tokens();
						
						String InfNFeId = eElement.getAttribute("Id");
						infoVM.setInfNFeId(InfNFeId.replaceAll("NFe", ""));
						xmlDocument.setInfNFeId(InfNFeId.replaceAll("NFe", ""));
						
						List<XMLDocument> xmlDocumentList = xmlDocumentRepository.findByInfNFeId(infoVM.getInfNFeId());
						if (xmlDocumentList.size()>0) {
							xmlDocument = xmlDocumentList.get(0);
							if (xmlDocument.getToken() != null && xmlDocument.getToken().isAuthorised() ) {
								infoVM.setAuthorised(true);
								token = xmlDocument.getToken();
							} else if (xmlDocument.getToken() != null) {
								token  = xmlDocument.getToken();
							}
						}
						xmlDocument.setnNF(Integer.parseInt(eElement.getElementsByTagName("nNF").item(0).getTextContent()));
						xmlDocument.setxNome(eElement.getElementsByTagName("xNome").item(0).getTextContent());
						
						infoVM.setnNF(Integer.parseInt(eElement.getElementsByTagName("nNF").item(0).getTextContent()));
						infoVM.setxNome(eElement.getElementsByTagName("xNome").item(0).getTextContent());
						if(eElement.getElementsByTagName("dEmi").item(0) != null) {
							infoVM.setdEmi(eElement.getElementsByTagName("dEmi").item(0).getTextContent());	
							xmlDocument.setdEmi(eElement.getElementsByTagName("dEmi").item(0).getTextContent());
						}
                        System.out.println("QQQQ Dest @@@@ :: \n" + eElement.getAttribute("dest")); 
						if (isValidSignature(arrayOutputStream)) {
							String emailNode = eElement.getElementsByTagName("email").item(0).getTextContent();
							String[] emailNodeList = null;
							if (emailNode != null) {
								emailNodeList = emailNode.split(";");
							}
							EmailVM emailVM = new EmailVM();
							UUID idOne = UUID.randomUUID();
							emailVM.setFrom("admin@mail.com");
							//emailVM.setTo(emailNodeList[0]);
							emailVM.setTo("ranajitmahakunde2010@gmail.com");
							String mailBody = "<div>Hi,\n"+ idOne + "</div>"; 
							emailVM.setMailBody(mailBody);
							emailVM.setSubject("XML validation");
							sendEmail.sendEmail(emailVM);
							
							if(eElement.getElementsByTagName("CNPJ").item(0) != null) {
								token.setDocument(eElement.getElementsByTagName("CNPJ").item(0).getTextContent());
								xmlDocument.setDocument(eElement.getElementsByTagName("CNPJ").item(0).getTextContent());
							} else if(eElement.getElementsByTagName("CPF").item(0) != null) {
								token.setDocument(eElement.getElementsByTagName("CPF").item(0).getTextContent());
								xmlDocument.setDocument(eElement.getElementsByTagName("CPF").item(0).getTextContent());
							}
							

							token.setRecipient(eElement.getElementsByTagName("xNome").item(0).getTextContent());
							token.setCreatedAt(new Date());
							token.setUpdatedAt(new Date());
						
							token.setEmail(emailNodeList[0]);
							token.setToken(""+idOne);
							token.setUser(user);
							token.setAuthorised(false);
							infoVM.setStatus("Valid");
							xmlDocument.setStatus("Valid");
							xmlDocument.setToken(token);
							if (!infoVM.isAuthorised()) {
								tokenRepository.save(token);
								xmlDocumentRepository.save(xmlDocument);
							}
							infoVM.setId(xmlDocument.getId());
						} else {
							infoVM.setStatus("Invalid");
							xmlDocument.setStatus("Invalid");
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


	public MessageVM uploadXml(Long userId, MultipartFile file) {
		System.out.println("userId :: " + userId);
		AuthUser user = authUserRepository.findOne(userId);
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
				Tokens token = new Tokens();
				XMLDocument xmlDocument = new XMLDocument();
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
					NFEInfoVM infoVM =new NFEInfoVM(); 
					String InfNFeId = eElement.getAttribute("Id");
					infoVM.setInfNFeId(InfNFeId.replaceAll("NFe", ""));
					List<XMLDocument> xmlDocumentList = xmlDocumentRepository.findByInfNFeId(infoVM.getInfNFeId());
					if (xmlDocumentList.size()>0) {
						xmlDocument = xmlDocumentList.get(0);
						if (xmlDocument.getToken() != null && xmlDocument.getToken().isAuthorised() ) {
							infoVM.setAuthorised(true);
							token = xmlDocument.getToken();
						}  else if (xmlDocument.getToken() != null) {
							token  = xmlDocument.getToken();
						}
						
					}
						
					
					infoVM.setnNF(Integer.parseInt(eElement.getElementsByTagName("nNF").item(0).getTextContent()));
					infoVM.setxNome(eElement.getElementsByTagName("xNome").item(0).getTextContent());
					
					xmlDocument.setInfNFeId(InfNFeId.replaceAll("NFe", ""));
					xmlDocument.setnNF(Integer.parseInt(eElement.getElementsByTagName("nNF").item(0).getTextContent()));
					xmlDocument.setxNome(eElement.getElementsByTagName("xNome").item(0).getTextContent());
					
					if(eElement.getElementsByTagName("dEmi").item(0) != null) {
						infoVM.setdEmi(eElement.getElementsByTagName("dEmi").item(0).getTextContent());	
						xmlDocument.setdEmi(eElement.getElementsByTagName("dEmi").item(0).getTextContent());	
					}
					
					System.out.println("QQQQ Dest @@@@ :: \n" + eElement.getElementsByTagName("email").item(0).getTextContent()); 
						
					if (isValidSignature(arrayOutputStream)) {
						
						String emailNode = eElement.getElementsByTagName("email").item(0).getTextContent();
						String[] emailNodeList = null;
						if (emailNode != null) {
							emailNodeList = emailNode.split(";");
						}
						xmlDocument.setStatus("Valid");
						EmailVM emailVM = new EmailVM();
						UUID idOne = UUID.randomUUID();
						emailVM.setFrom("admin@mail.com");
						//emailVM.setTo(emailNodeList[0]);
						emailVM.setTo("ranajitmahakunde2010@gmail.com");
						String mailBody = "<div>Hi,\n"+ idOne + "</div>"; 
						emailVM.setMailBody(mailBody);
						emailVM.setSubject("XML validation");
						if (!infoVM.isAuthorised())
						 sendEmail.sendEmail(emailVM);

						if(eElement.getElementsByTagName("CNPJ").item(0) != null) {
							token.setDocument(eElement.getElementsByTagName("CNPJ").item(0).getTextContent());
							xmlDocument.setDocument(eElement.getElementsByTagName("CNPJ").item(0).getTextContent());
						} else if(eElement.getElementsByTagName("CPF").item(0) != null) {
							token.setDocument(eElement.getElementsByTagName("CPF").item(0).getTextContent());
							xmlDocument.setDocument(eElement.getElementsByTagName("CPF").item(0).getTextContent());
						}

						token.setRecipient(eElement.getElementsByTagName("xNome").item(0).getTextContent());
						token.setCreatedAt(new Date());
						token.setUpdatedAt(new Date());
					
						token.setEmail(emailNodeList[0]);
						token.setToken(""+idOne);
						token.setUser(user);
						token.setAuthorised(false);
							
						xmlDocument.setToken(token);
						xmlDocument.setUser(user);
						xmlDocument.setCreatedAt(new Date());
						xmlDocument.setUpdatedAt(new Date());
						if (!infoVM.isAuthorised()) {
							tokenRepository.save(token);
							xmlDocumentRepository.save(xmlDocument);
						}
						infoVM.setId(xmlDocument.getId());
						infoVM.setStatus("Valid");
					} else {
						infoVM.setStatus("Invalid");
						xmlDocument.setStatus("Invalid");
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

	public MessageVM getUploadedDocument(Long userId) {
		AuthUser user = authUserRepository.findOne(userId);
		MessageVM messageVm = new MessageVM();
		List< XMLDocument>  xmlDocumentList = xmlDocumentRepository.findByUser(user);
		List<NFEInfoVM> nfeInfoVMList = new ArrayList<>();
		for (XMLDocument xmlDocument : xmlDocumentList) {
			Tokens token = xmlDocument.getToken();
			NFEInfoVM vm = new NFEInfoVM();
			vm.setId(xmlDocument.getId());
			vm.setdEmi(xmlDocument.getdEmi());
			vm.setInfNFeId(xmlDocument.getInfNFeId());
			vm.setnNF(xmlDocument.getnNF());
			vm.setStatus(xmlDocument.getStatus());
			vm.setxNome(xmlDocument.getxNome());
			if (token != null) {
				vm.setAuthorised(token.isAuthorised());
			} else {
				vm.setAuthorised(false);	
			}
			nfeInfoVMList.add(vm);	
		}
		messageVm.setData(nfeInfoVMList);
		return messageVm;
	}
}