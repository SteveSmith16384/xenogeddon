/**
 * gl_face_type.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.model.collada.schema;

import com.jmex.xml.types.SchemaString;

public class gl_face_type extends SchemaString {
	public static final int EFRONT = 0; /* FRONT */
	public static final int EBACK = 1; /* BACK */
	public static final int EFRONT_AND_BACK = 2; /* FRONT_AND_BACK */

	public static String[] sEnumValues = {
		"FRONT",
		"BACK",
		"FRONT_AND_BACK",
	};

	public gl_face_type() {
		super();
	}

	public gl_face_type(String newValue) {
		super(newValue);
		validate();
	}

	public gl_face_type(SchemaString newValue) {
		super(newValue);
		validate();
	}

	public static int getEnumerationCount() {
		return sEnumValues.length;
	}

	public static String getEnumerationValue(int index) {
		return sEnumValues[index];
	}

	public static boolean isValidEnumerationValue(String val) {
		for (int i = 0; i < sEnumValues.length; i++) {
			if (val.equals(sEnumValues[i]))
				return true;
		}
		return false;
	}

	public void validate() {

		if (!isValidEnumerationValue(toString()))
			throw new com.jmex.xml.xml.XmlException("Value of gl_face_type is invalid.");
	}
}
