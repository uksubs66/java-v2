
package com.ngn.services._2007._03._20.scoredata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PlayType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PlayType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Tournament"/>
 *     &lt;enumeration value="Leisure"/>
 *     &lt;enumeration value="League"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PlayType")
@XmlEnum
public enum PlayType {

    @XmlEnumValue("Tournament")
    TOURNAMENT("Tournament"),
    @XmlEnumValue("Leisure")
    LEISURE("Leisure"),
    @XmlEnumValue("League")
    LEAGUE("League");
    private final String value;

    PlayType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PlayType fromValue(String v) {
        for (PlayType c: PlayType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
