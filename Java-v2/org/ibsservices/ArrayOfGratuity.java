
package org.ibsservices;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfGratuity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfGratuity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Gratuity" type="{http://ibsservices.org/}Gratuity" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfGratuity", propOrder = {
    "gratuity"
})
public class ArrayOfGratuity {

    @XmlElement(name = "Gratuity", nillable = true)
    protected List<Gratuity> gratuity;

    /**
     * Gets the value of the gratuity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gratuity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGratuity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gratuity }
     * 
     * 
     */
    public List<Gratuity> getGratuity() {
        if (gratuity == null) {
            gratuity = new ArrayList<Gratuity>();
        }
        return this.gratuity;
    }

}
