
package com.ngn.services._2007._03._20.scoreservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ngn.services._2007._03._20.headerdata.Credentials;
import com.ngn.services._2007._03._20.scoredata.ScoreList;


/**
 * <p>Java class for UpdateScoresRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateScoresRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Credentials" type="{http://services.ngn.com/2007/03/20/HeaderData}Credentials" minOccurs="0"/>
 *         &lt;element name="ScoreList" type="{http://services.ngn.com/2007/03/20/ScoreData}ScoreList" minOccurs="0"/>
 *         &lt;element name="PostScoreProcess" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CheckForDuplicateScores" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateScoresRequest", propOrder = {
    "transactionId",
    "credentials",
    "scoreList",
    "postScoreProcess",
    "checkForDuplicateScores"
})
public class UpdateScoresRequest {

    @XmlElement(name = "TransactionId")
    protected String transactionId;
    @XmlElement(name = "Credentials")
    protected Credentials credentials;
    @XmlElement(name = "ScoreList")
    protected ScoreList scoreList;
    @XmlElement(name = "PostScoreProcess")
    protected boolean postScoreProcess;
    @XmlElement(name = "CheckForDuplicateScores")
    protected boolean checkForDuplicateScores;

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
     * Gets the value of the credentials property.
     * 
     * @return
     *     possible object is
     *     {@link Credentials }
     *     
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the value of the credentials property.
     * 
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *     
     */
    public void setCredentials(Credentials value) {
        this.credentials = value;
    }

    /**
     * Gets the value of the scoreList property.
     * 
     * @return
     *     possible object is
     *     {@link ScoreList }
     *     
     */
    public ScoreList getScoreList() {
        return scoreList;
    }

    /**
     * Sets the value of the scoreList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoreList }
     *     
     */
    public void setScoreList(ScoreList value) {
        this.scoreList = value;
    }

    /**
     * Gets the value of the postScoreProcess property.
     * 
     */
    public boolean isPostScoreProcess() {
        return postScoreProcess;
    }

    /**
     * Sets the value of the postScoreProcess property.
     * 
     */
    public void setPostScoreProcess(boolean value) {
        this.postScoreProcess = value;
    }

    /**
     * Gets the value of the checkForDuplicateScores property.
     * 
     */
    public boolean isCheckForDuplicateScores() {
        return checkForDuplicateScores;
    }

    /**
     * Sets the value of the checkForDuplicateScores property.
     * 
     */
    public void setCheckForDuplicateScores(boolean value) {
        this.checkForDuplicateScores = value;
    }

}
