
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventMemberStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EventMemberStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Participant"/>
 *     &lt;enumeration value="Waiting"/>
 *     &lt;enumeration value="Unpaired"/>
 *     &lt;enumeration value="Error"/>
 *     &lt;enumeration value="Pending"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EventMemberStatusType")
@XmlEnum
public enum EventMemberStatusType {

    @XmlEnumValue("Participant")
    PARTICIPANT("Participant"),
    @XmlEnumValue("Waiting")
    WAITING("Waiting"),
    @XmlEnumValue("Unpaired")
    UNPAIRED("Unpaired"),
    @XmlEnumValue("Error")
    ERROR("Error"),
    @XmlEnumValue("Pending")
    PENDING("Pending");
    private final String value;

    EventMemberStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EventMemberStatusType fromValue(String v) {
        for (EventMemberStatusType c: EventMemberStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
