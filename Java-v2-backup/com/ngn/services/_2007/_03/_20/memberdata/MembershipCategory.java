
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MembershipCategory.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MembershipCategory">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Junior Boys"/>
 *     &lt;enumeration value="Junior Girls"/>
 *     &lt;enumeration value="Senior Men"/>
 *     &lt;enumeration value="Senior Women"/>
 *     &lt;enumeration value="Men"/>
 *     &lt;enumeration value="Women"/>
 *     &lt;enumeration value="Staff"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MembershipCategory")
@XmlEnum
public enum MembershipCategory {

    @XmlEnumValue("Junior Boys")
    JUNIOR_BOYS("Junior Boys"),
    @XmlEnumValue("Junior Girls")
    JUNIOR_GIRLS("Junior Girls"),
    @XmlEnumValue("Senior Men")
    SENIOR_MEN("Senior Men"),
    @XmlEnumValue("Senior Women")
    SENIOR_WOMEN("Senior Women"),
    @XmlEnumValue("Men")
    MEN("Men"),
    @XmlEnumValue("Women")
    WOMEN("Women"),
    @XmlEnumValue("Staff")
    STAFF("Staff");
    private final String value;

    MembershipCategory(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MembershipCategory fromValue(String v) {
        for (MembershipCategory c: MembershipCategory.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
