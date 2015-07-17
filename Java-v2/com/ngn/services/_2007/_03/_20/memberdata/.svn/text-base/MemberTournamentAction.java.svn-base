
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberTournamentAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MemberTournamentAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Add"/>
 *     &lt;enumeration value="Update"/>
 *     &lt;enumeration value="Fetch"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MemberTournamentAction")
@XmlEnum
public enum MemberTournamentAction {

    @XmlEnumValue("Add")
    ADD("Add"),
    @XmlEnumValue("Update")
    UPDATE("Update"),
    @XmlEnumValue("Fetch")
    FETCH("Fetch");
    private final String value;

    MemberTournamentAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MemberTournamentAction fromValue(String v) {
        for (MemberTournamentAction c: MemberTournamentAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
