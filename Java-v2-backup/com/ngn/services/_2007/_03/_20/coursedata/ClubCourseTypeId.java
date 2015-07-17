
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClubCourseTypeId.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClubCourseTypeId">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Primary"/>
 *     &lt;enumeration value="Secondary"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClubCourseTypeId")
@XmlEnum
public enum ClubCourseTypeId {

    @XmlEnumValue("Primary")
    PRIMARY("Primary"),
    @XmlEnumValue("Secondary")
    SECONDARY("Secondary");
    private final String value;

    ClubCourseTypeId(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClubCourseTypeId fromValue(String v) {
        for (ClubCourseTypeId c: ClubCourseTypeId.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
