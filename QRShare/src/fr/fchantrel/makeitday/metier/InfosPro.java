package fr.fchantrel.makeitday.metier;
/**
 * 
 */

/**
 * @author FredASI
 * Bean métier contenant les données à afficher
 *
 */
public class InfosPro {
	
	private String identifiantListView;
	
	public String getIdentifiantListView() {
		return identifiantListView;
	}

	public void setIdentifiantListView(String identifiantListView) {
		this.identifiantListView = identifiantListView;
	}

	private String denomination;
	
	private int noImage;
	
	public int getNoImage() {
		return noImage;
	}

	public void setNoImage(int noImage) {
		this.noImage = noImage;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getNoTelephone() {
		return noTelephone;
	}

	public void setNoTelephone(String noTelephone) {
		this.noTelephone = noTelephone;
	}

	public String getLibelleAdresseComplete() {
		return libelleAdresseComplete;
	}

	public void setLibelleAdresseComplete(String libelleAdresseComplete) {
		this.libelleAdresseComplete = libelleAdresseComplete;
	}

	private String noTelephone;
	
	private String libelleAdresseComplete;

}
