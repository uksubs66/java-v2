
package com.ngn.services._2007._03._20.scoredata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScoreIdList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScoreIdList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ScoreId" type="{http://services.ngn.com/2007/03/20/ScoreData}ScoreIdInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScoreIdList", propOrder = {
    "scoreId"
})
public class ScoreIdList {

    @XmlElement(name = "ScoreId")
    protected List<ScoreIdInfo> scoreId;

    /**
     * Gets the value of the scoreId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scoreId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScoreId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScoreIdInfo }
     * 
     * 
     */
    public List<ScoreIdInfo> getScoreId() {
        if (scoreId == null) {
            scoreId = new ArrayList<ScoreIdInfo>();
        }
        return this.scoreId;
    }

}