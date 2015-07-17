
package com.ngn.services._2007._03._20.scoredata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScoreType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScoreType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Away"/>
 *     &lt;enumeration value="Away Internet"/>
 *     &lt;enumeration value="Combined Nines"/>
 *     &lt;enumeration value="Combined Nines Internet"/>
 *     &lt;enumeration value="Home"/>
 *     &lt;enumeration value="Internet"/>
 *     &lt;enumeration value="Penalty"/>
 *     &lt;enumeration value="Tournament"/>
 *     &lt;enumeration value="Tournament Internet"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ScoreType")
@XmlEnum
public enum ScoreType {

    @XmlEnumValue("Away")
    AWAY("Away"),
    @XmlEnumValue("Away Internet")
    AWAY_INTERNET("Away Internet"),
    @XmlEnumValue("Combined Nines")
    COMBINED_NINES("Combined Nines"),
    @XmlEnumValue("Combined Nines Internet")
    COMBINED_NINES_INTERNET("Combined Nines Internet"),
    @XmlEnumValue("Home")
    HOME("Home"),
    @XmlEnumValue("Internet")
    INTERNET("Internet"),
    @XmlEnumValue("Penalty")
    PENALTY("Penalty"),
    @XmlEnumValue("Tournament")
    TOURNAMENT("Tournament"),
    @XmlEnumValue("Tournament Internet")
    TOURNAMENT_INTERNET("Tournament Internet");
    private final String value;

    ScoreType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScoreType fromValue(String v) {
        for (ScoreType c: ScoreType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
