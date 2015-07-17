
package com.ngn.services._2007._03._20.handicapdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HandicapRecordType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="HandicapRecordType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Official"/>
 *     &lt;enumeration value="Trend"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "HandicapRecordType")
@XmlEnum
public enum HandicapRecordType {

    @XmlEnumValue("Official")
    OFFICIAL("Official"),
    @XmlEnumValue("Trend")
    TREND("Trend");
    private final String value;

    HandicapRecordType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static HandicapRecordType fromValue(String v) {
        for (HandicapRecordType c: HandicapRecordType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
