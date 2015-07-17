
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TeeBoxScorecardType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TeeBoxScorecardType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Mens"/>
 *     &lt;enumeration value="Womens"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TeeBoxScorecardType")
@XmlEnum
public enum TeeBoxScorecardType {

    @XmlEnumValue("Mens")
    MENS("Mens"),
    @XmlEnumValue("Womens")
    WOMENS("Womens");
    private final String value;

    TeeBoxScorecardType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TeeBoxScorecardType fromValue(String v) {
        for (TeeBoxScorecardType c: TeeBoxScorecardType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
