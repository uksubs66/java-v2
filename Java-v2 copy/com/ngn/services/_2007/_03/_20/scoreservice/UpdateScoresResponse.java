
package com.ngn.services._2007._03._20.scoreservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.scoredata.ScoreResponseList;


/**
 * <p>Java class for UpdateScoresResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateScoresResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ScoreResponseList" type="{http://services.ngn.com/2007/03/20/ScoreData}ScoreResponseList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateScoresResponse", propOrder = {
    "transactionId",
    "scoreResponseList"
})
public class UpdateScoresResponse {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "ScoreResponseList")
    protected ScoreResponseList scoreResponseList;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the scoreResponseList property.
     * 
     * @return
     *     possible object is
     *     {@link ScoreResponseList }
     *     
     */
    public ScoreResponseList getScoreResponseList() {
        return scoreResponseList;
    }

    /**
     * Sets the value of the scoreResponseList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoreResponseList }
     *     
     */
    public void setScoreResponseList(ScoreResponseList value) {
        this.scoreResponseList = value;
    }

}
