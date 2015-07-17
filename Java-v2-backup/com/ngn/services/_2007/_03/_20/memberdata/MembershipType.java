
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MembershipType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MembershipType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Full Member"/>
 *     &lt;enumeration value="Trial Member with GameTracker"/>
 *     &lt;enumeration value="Trial Member"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MembershipType")
@XmlEnum
public enum MembershipType {

    @XmlEnumValue("Full Member")
    FULL_MEMBER("Full Member"),
    @XmlEnumValue("Trial Member with GameTracker")
    TRIAL_MEMBER_WITH_GAME_TRACKER("Trial Member with GameTracker"),
    @XmlEnumValue("Trial Member")
    TRIAL_MEMBER("Trial Member");
    private final String value;

    MembershipType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MembershipType fromValue(String v) {
        for (MembershipType c: MembershipType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
