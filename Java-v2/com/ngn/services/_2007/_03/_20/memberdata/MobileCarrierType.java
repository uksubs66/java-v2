
package com.ngn.services._2007._03._20.memberdata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MobileCarrierType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MobileCarrierType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="ATT"/>
 *     &lt;enumeration value="VerizonWireless"/>
 *     &lt;enumeration value="TMobile"/>
 *     &lt;enumeration value="Sprint"/>
 *     &lt;enumeration value="NextTel"/>
 *     &lt;enumeration value="BoostMobile"/>
 *     &lt;enumeration value="Alltel"/>
 *     &lt;enumeration value="USCellular"/>
 *     &lt;enumeration value="VirginMobile"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MobileCarrierType")
@XmlEnum
public enum MobileCarrierType {

    @XmlEnumValue("None")
    NONE("None"),
    ATT("ATT"),
    @XmlEnumValue("VerizonWireless")
    VERIZON_WIRELESS("VerizonWireless"),
    @XmlEnumValue("TMobile")
    T_MOBILE("TMobile"),
    @XmlEnumValue("Sprint")
    SPRINT("Sprint"),
    @XmlEnumValue("NextTel")
    NEXT_TEL("NextTel"),
    @XmlEnumValue("BoostMobile")
    BOOST_MOBILE("BoostMobile"),
    @XmlEnumValue("Alltel")
    ALLTEL("Alltel"),
    @XmlEnumValue("USCellular")
    US_CELLULAR("USCellular"),
    @XmlEnumValue("VirginMobile")
    VIRGIN_MOBILE("VirginMobile");
    private final String value;

    MobileCarrierType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MobileCarrierType fromValue(String v) {
        for (MobileCarrierType c: MobileCarrierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
