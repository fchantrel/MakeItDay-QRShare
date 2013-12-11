/**
 * 
 */
package fr.fchantrel.makeitday.parser.recherche;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.fchantrel.makeitday.metier.InfosPro;

/**
 * @author FredASI Parser utilisé pour le parsing de la réponse du
 *         ws-rest-recherche
 * 
 */
public class XMLRechercheParser extends DefaultHandler {

	InfosPro proValues = null;
	
	public InfosPro getProValues() {
		return proValues;
	}

	public void setProValues(InfosPro proValues) {
		this.proValues = proValues;
	}

	// string builder acts as a buffer
	StringBuilder builder;

	// Initialize the arraylist
	// @throws SAXException

	@Override
	public void startDocument() throws SAXException {

		/******* Cree InfosPro pour stocker les valeurs du pro ******/
		proValues = new InfosPro();
	}

	// Initialize the temp XmlValuesModel object which will hold the parsed info
	// and the string builder that will store the read characters
	// @param uri
	// @param localName ( Parsed Node name will come in localName )
	// @param qName
	// @param attributes
	// @throws SAXException

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		/**** When New XML Node initiating to parse this function called *****/
		// Create StringBuilder object to store xml node value
		builder = new StringBuilder();

		if (localName.equals("info_pro")) {

		} else if (localName.equals("denomination")) {

		} else if (localName.equals("numero_formate")) {

		} else if (localName.equals("libelle_adresse_complete")) {

		}
	}

	// Finished reading the login tag, add it to arraylist
	// @param uri
	// @param localName
	// @param qName
	// @throws SAXException

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (localName.equalsIgnoreCase("denomination")) {
			proValues.setDenomination(builder.toString());
		} else if (localName.equalsIgnoreCase("numero_formate")) {
			proValues.setNoTelephone(builder.toString());
		} else if (localName.equalsIgnoreCase("libelle_adresse_complete")) {
			proValues.setLibelleAdresseComplete(builder.toString());
		} else if (localName.equals("info_pro")) {
			/** lecture des infos du pro finie, rien a faire **/
		}
	}

	// Read the value of each xml NODE
	// @param ch
	// @param start
	// @param length
	// @throws SAXException

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		/****** Read the characters and append them to the buffer ******/
		String tempString = new String(ch, start, length);
		builder.append(tempString);
	}

}
