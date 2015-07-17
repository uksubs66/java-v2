
package org.ibsservices;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BalanceStatusTypes.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BalanceStatusTypes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="All"/>
 *     &lt;enumeration value="LessThanZero"/>
 *     &lt;enumeration value="Zero"/>
 *     &lt;enumeration value="GreaterThanZero"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BalanceStatusTypes")
@XmlEnum
public enum BalanceStatusTypes {

    @XmlEnumValue("All")
    ALL("All"),
    @XmlEnumValue("LessThanZero")
    LESS_THAN_ZERO("LessThanZero"),
    @XmlEnumValue("Zero")
    ZERO("Zero"),
    @XmlEnumValue("GreaterThanZero")
    GREATER_THAN_ZERO("GreaterThanZero");
    private final String value;

    BalanceStatusTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BalanceStatusTypes fromValue(String v) {
        for (BalanceStatusTypes c: BalanceStatusTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
