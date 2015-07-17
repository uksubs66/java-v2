
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberHandicapType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MemberHandicapType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Official"/>
 *     &lt;enumeration value="Trend"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MemberHandicapType")
@XmlEnum
public enum MemberHandicapType {

    @XmlEnumValue("Official")
    OFFICIAL("Official"),
    @XmlEnumValue("Trend")
    TREND("Trend");
    private final String value;

    MemberHandicapType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MemberHandicapType fromValue(String v) {
        for (MemberHandicapType c: MemberHandicapType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
