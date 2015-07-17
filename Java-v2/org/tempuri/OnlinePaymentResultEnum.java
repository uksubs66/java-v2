
package org.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OnlinePaymentResultEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OnlinePaymentResultEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Failed"/>
 *     &lt;enumeration value="Successed"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OnlinePaymentResultEnum")
@XmlEnum
public enum OnlinePaymentResultEnum {

    @XmlEnumValue("Failed")
    FAILED("Failed"),
    @XmlEnumValue("Successed")
    SUCCESSED("Successed");
    private final String value;

    OnlinePaymentResultEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OnlinePaymentResultEnum fromValue(String v) {
        for (OnlinePaymentResultEnum c: OnlinePaymentResultEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
