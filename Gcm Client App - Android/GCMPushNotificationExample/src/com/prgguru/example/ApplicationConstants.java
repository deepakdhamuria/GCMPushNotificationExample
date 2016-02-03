package com.prgguru.example;

public interface ApplicationConstants {

	// Php Application URL to store Reg ID created
	static final String APP_SERVER_URL = "http://simplehrassist.esys.es/gcm.php?shareRegId=true";
	static final String TICKET_REQUEST_URL = "http://192.168.1.125:80/gcm/gcm.php?ticketRequested=true";
	static final String SEND_TICKET_RESPONSE_URL = "http://192.168.1.125:80/gcm/gcm.php?sendTicketResponse=true";

	// Google Project Number
	static final String GOOGLE_PROJ_ID = "531029028227";
	// Message Key
	static final String MSG_KEY = "m";

}
