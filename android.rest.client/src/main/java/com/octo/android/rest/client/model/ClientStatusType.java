package com.octo.android.rest.client.model;

import java.io.Serializable;


public enum ClientStatusType implements Serializable {

	/**
	 * La spec des differentes type de status: \\ngdata03\projets-versions-dsi-pf\Projets 2011\11MOBILE\12_CFD - STD\SuivDemandeEnLigne
	 */
	ACCEPTEE("Acceptée"),
	ENCOURS("en cours"),
	REFUSES("Refusée"),
	A_LETUDE("A l’étude"),
	SANS_SUITE("Sans suite"),
	ANNULEE("Annulée"),
	ENVOI_CONTRAT("Envoi contrat");

	private String status;

	private ClientStatusType(final String pValue) {
		this.status = pValue;
	}

	/**
	 * Accesseur en lecture pour le statut.
	 * 
	 * @return statut
	 */
	public String getStatus() {
		return status;
	}

	public static ClientStatusType fromString(String text) {
		if (text != null) {
			for (ClientStatusType type : ClientStatusType.values()) {
				if (text.equalsIgnoreCase(type.getStatus())) {
					return type;
				}
			}
		}
		return null;
	}
}