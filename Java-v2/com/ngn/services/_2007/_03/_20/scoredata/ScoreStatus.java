
package com.ngn.services._2007._03._20.scoredata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScoreStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScoreStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Current"/>
 *     &lt;enumeration value="Deleted"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ScoreStatus")
@XmlEnum
public enum ScoreStatus {

    @XmlEnumValue("Current")
    CURRENT("Current"),
    @XmlEnumValue("Deleted")
    DELETED("Deleted");
    private final String value;

    ScoreStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScoreStatus fromValue(String v) {
        for (ScoreStatus c: ScoreStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
