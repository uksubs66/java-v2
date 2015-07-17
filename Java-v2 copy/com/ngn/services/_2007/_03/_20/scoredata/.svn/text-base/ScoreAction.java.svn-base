
package com.ngn.services._2007._03._20.scoredata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScoreAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScoreAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Add"/>
 *     &lt;enumeration value="Update"/>
 *     &lt;enumeration value="Archive"/>
 *     &lt;enumeration value="Delete"/>
 *     &lt;enumeration value="Fetch"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ScoreAction")
@XmlEnum
public enum ScoreAction {

    @XmlEnumValue("Add")
    ADD("Add"),
    @XmlEnumValue("Update")
    UPDATE("Update"),
    @XmlEnumValue("Archive")
    ARCHIVE("Archive"),
    @XmlEnumValue("Delete")
    DELETE("Delete"),
    @XmlEnumValue("Fetch")
    FETCH("Fetch");
    private final String value;

    ScoreAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScoreAction fromValue(String v) {
        for (ScoreAction c: ScoreAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
