package org.mifosplatform.infrastructure.configuration.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationConstants {
	
	public final static String CONFIG_PROPERTY_IMPLICIT_ASSOCIATION="Implicit Association";
	public final static String CONFIG_PROPERTY_BALANCE_CHECK="Forcible Balance Check";
	public final static String CONFIG_PROPERTY_AUTO_RENEWAL="renewal";
	public final static String CONFIG_PROPERTY_LOGIN="Login";
	public final static String CONFIG_PROPERTY_DATEFORMAT="DateFormat";
	public final static String CONFIG_PROPERTY_ROUNDING="Rounding";
	public static final String CPE_TYPE="CPE_TYPE";
	public static final String CONFIR_PROPERTY_SALE="SALE";
	public static final String CONFIR_PROPERTY_OWN="OWN";

	public static final String ENABLED = "enabled";
	public static final String VALUE = "value";
	public static final String ID = "id";
	public static final String CONFIGURATION_RESOURCE_NAME = "globalConfiguration";
	public static final Set<String> UPDATE_CONFIGURATION_DATA_PARAMETERS = new HashSet<String>(Arrays.asList(ENABLED, VALUE));


}
