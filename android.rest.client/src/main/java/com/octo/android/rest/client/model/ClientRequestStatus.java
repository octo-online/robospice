package com.octo.android.rest.client.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Representation du suivi de demande de crédit CLIENT.
 * 
 * @author LUNVENC
 * @author <a href="mailto:cetelem.mobilite.projet@octo.com">Projet Mobilite Octo</a>
 */
public class ClientRequestStatus implements Serializable {

	private static final long serialVersionUID = 6059164762223567755L;

	/**
	 * Montant
	 */
	private BigDecimal amount;

	/**
	 * La date de la demande
	 */
	private String date;

	/**
	 * Le status de la demande Ex : Etude
	 */
	@JsonDeserialize(using = StatusTypeDeserializer.class)
	private ClientStatusType status;

	/**
	 * Un texte décrivant l’état de la demande en cours.
	 */
	private String statusDescription;

	/**
	 * La duration de la demande.
	 */
	private String duration;

	/**
	 * Le nom de la catégorie de la demande en cours ex : Pret Auto.
	 */
	private String sectionTitle;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ClientStatusType getStatus() {
		return status;
	}

	public void setStatus(ClientStatusType status) {
		this.status = status;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

}
