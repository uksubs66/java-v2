
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberAction.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MemberAction">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Add"/>
 *     &lt;enumeration value="Update"/>
 *     &lt;enumeration value="Transfer"/>
 *     &lt;enumeration value="Archive"/>
 *     &lt;enumeration value="Restore"/>
 *     &lt;enumeration value="Delete"/>
 *     &lt;enumeration value="AddMembership"/>
 *     &lt;enumeration value="Fetch"/>
 *     &lt;enumeration value="CancelMembership"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MemberAction")
@XmlEnum
public enum MemberAction {

    @XmlEnumValue("Add")
    ADD("Add"),
    @XmlEnumValue("Update")
    UPDATE("Update"),
    @XmlEnumValue("Transfer")
    TRANSFER("Transfer"),
    @XmlEnumValue("Archive")
    ARCHIVE("Archive"),
    @XmlEnumValue("Restore")
    RESTORE("Restore"),
    @XmlEnumValue("Delete")
    DELETE("Delete"),
    @XmlEnumValue("AddMembership")
    ADD_MEMBERSHIP("AddMembership"),
    @XmlEnumValue("Fetch")
    FETCH("Fetch"),
    @XmlEnumValue("CancelMembership")
    CANCEL_MEMBERSHIP("CancelMembership");
    private final String value;

    MemberAction(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MemberAction fromValue(String v) {
        for (MemberAction c: MemberAction.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
