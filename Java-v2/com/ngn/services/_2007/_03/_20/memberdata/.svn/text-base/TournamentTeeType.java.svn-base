
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TournamentTeeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TournamentTeeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Back"/>
 *     &lt;enumeration value="Middle"/>
 *     &lt;enumeration value="Forward"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TournamentTeeType")
@XmlEnum
public enum TournamentTeeType {

    @XmlEnumValue("Back")
    BACK("Back"),
    @XmlEnumValue("Middle")
    MIDDLE("Middle"),
    @XmlEnumValue("Forward")
    FORWARD("Forward"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    TournamentTeeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TournamentTeeType fromValue(String v) {
        for (TournamentTeeType c: TournamentTeeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
