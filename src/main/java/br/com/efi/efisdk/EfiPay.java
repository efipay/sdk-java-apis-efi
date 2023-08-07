package br.com.efi.efisdk;

/**
 * This class extends Endpoins class.
 * @author Consultoria Técnica
 */

import java.util.Map;

import org.json.JSONObject;

public class EfiPay extends Endpoints{
	public EfiPay(JSONObject options) throws Exception {
		super(options);
	}	
	public EfiPay(Map<String, Object> options) throws Exception {
		super(options);
	}
}
