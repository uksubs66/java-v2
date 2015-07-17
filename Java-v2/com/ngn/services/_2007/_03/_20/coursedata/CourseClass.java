
package com.ngn.services._2007._03._20.coursedata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CourseClass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CourseClass">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Public"/>
 *     &lt;enumeration value="Private"/>
 *     &lt;enumeration value="Semi-Private"/>
 *     &lt;enumeration value="Municipal"/>
 *     &lt;enumeration value="Military"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CourseClass")
@XmlEnum
public enum CourseClass {

    @XmlEnumValue("Public")
    PUBLIC("Public"),
    @XmlEnumValue("Private")
    PRIVATE("Private"),
    @XmlEnumValue("Semi-Private")
    SEMI_PRIVATE("Semi-Private"),
    @XmlEnumValue("Municipal")
    MUNICIPAL("Municipal"),
    @XmlEnumValue("Military")
    MILITARY("Military");
    private final String value;

    CourseClass(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CourseClass fromValue(String v) {
        for (CourseClass c: CourseClass.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
