
package com.ngn.services._2007._03._20.scoredata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ScoreInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScoreInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PlayDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="CourseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TeeBoxName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CourseId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TeeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Slope" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Rating" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Par" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Yardage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Gross" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ESC" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PlayType" type="{http://services.ngn.com/2007/03/20/ScoreData}PlayType"/>
 *         &lt;element name="HolesPlayed" type="{http://services.ngn.com/2007/03/20/ScoreData}HolesPlayed"/>
 *         &lt;element name="ScoreType" type="{http://services.ngn.com/2007/03/20/ScoreData}ScoreType"/>
 *         &lt;element name="IsLocal" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PlayedWithClubMember" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Attestor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsHoleByHole" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsUsedInCalc" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="HoleScores" type="{http://services.ngn.com/2007/03/20/ScoreData}ArrayOfHoleScoreInfo"/>
 *         &lt;element name="CreatedOn" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="UpdatedOn" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="ScoreId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Status" type="{http://services.ngn.com/2007/03/20/ScoreData}ScoreStatus"/>
 *         &lt;element name="Action" type="{http://services.ngn.com/2007/03/20/ScoreData}ScoreAction"/>
 *         &lt;element name="NetworkId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *       &lt;attribute name="SourceScoreId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SourceUserId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TransactionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScoreInfo", propOrder = {
    "playDate",
    "courseName",
    "teeBoxName",
    "courseId",
    "teeId",
    "slope",
    "rating",
    "par",
    "yardage",
    "gross",
    "esc",
    "playType",
    "holesPlayed",
    "scoreType",
    "isLocal",
    "playedWithClubMember",
    "attestor",
    "isHoleByHole",
    "isUsedInCalc",
    "holeScores",
    "createdOn",
    "updatedOn",
    "scoreId",
    "status",
    "action",
    "networkId"
})
public class ScoreInfo {

    @XmlElement(name = "PlayDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar playDate;
    @XmlElement(name = "CourseName")
    protected String courseName;
    @XmlElement(name = "TeeBoxName")
    protected String teeBoxName;
    @XmlElement(name = "CourseId")
    protected int courseId;
    @XmlElement(name = "TeeId")
    protected int teeId;
    @XmlElement(name = "Slope")
    protected int slope;
    @XmlElement(name = "Rating")
    protected int rating;
    @XmlElement(name = "Par", required = true, type = Integer.class, nillable = true)
    protected Integer par;
    @XmlElement(name = "Yardage", required = true, type = Integer.class, nillable = true)
    protected Integer yardage;
    @XmlElement(name = "Gross")
    protected int gross;
    @XmlElement(name = "ESC", required = true, type = Integer.class, nillable = true)
    protected Integer esc;
    @XmlElement(name = "PlayType", required = true)
    protected PlayType playType;
    @XmlElement(name = "HolesPlayed", required = true)
    protected String holesPlayed;
    @XmlElement(name = "ScoreType", required = true)
    protected ScoreType scoreType;
    @XmlElement(name = "IsLocal", required = true, type = Boolean.class, nillable = true)
    protected Boolean isLocal;
    @XmlElement(name = "PlayedWithClubMember", required = true, type = Boolean.class, nillable = true)
    protected Boolean playedWithClubMember;
    @XmlElement(name = "Attestor", required = true, nillable = true)
    protected String attestor;
    @XmlElement(name = "IsHoleByHole")
    protected boolean isHoleByHole;
    @XmlElement(name = "IsUsedInCalc")
    protected boolean isUsedInCalc;
    @XmlElement(name = "HoleScores", required = true, nillable = true)
    protected ArrayOfHoleScoreInfo holeScores;
    @XmlElement(name = "CreatedOn", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdOn;
    @XmlElement(name = "UpdatedOn", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar updatedOn;
    @XmlElement(name = "ScoreId")
    protected long scoreId;
    @XmlElement(name = "Status", required = true)
    protected ScoreStatus status;
    @XmlElement(name = "Action", required = true)
    protected ScoreAction action;
    @XmlElement(name = "NetworkId")
    protected int networkId;
    @XmlAttribute(name = "SourceScoreId")
    protected String sourceScoreId;
    @XmlAttribute(name = "SourceUserId")
    protected String sourceUserId;
    @XmlAttribute(name = "TransactionId")
    protected String transactionId;

    /**
     * Gets the value of the playDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPlayDate() {
        return playDate;
    }

    /**
     * Sets the value of the playDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPlayDate(XMLGregorianCalendar value) {
        this.playDate = value;
    }

    /**
     * Gets the value of the courseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Sets the value of the courseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourseName(String value) {
        this.courseName = value;
    }

    /**
     * Gets the value of the teeBoxName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTeeBoxName() {
        return teeBoxName;
    }

    /**
     * Sets the value of the teeBoxName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTeeBoxName(String value) {
        this.teeBoxName = value;
    }

    /**
     * Gets the value of the courseId property.
     * 
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     * 
     */
    public void setCourseId(int value) {
        this.courseId = value;
    }

    /**
     * Gets the value of the teeId property.
     * 
     */
    public int getTeeId() {
        return teeId;
    }

    /**
     * Sets the value of the teeId property.
     * 
     */
    public void setTeeId(int value) {
        this.teeId = value;
    }

    /**
     * Gets the value of the slope property.
     * 
     */
    public int getSlope() {
        return slope;
    }

    /**
     * Sets the value of the slope property.
     * 
     */
    public void setSlope(int value) {
        this.slope = value;
    }

    /**
     * Gets the value of the rating property.
     * 
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     * 
     */
    public void setRating(int value) {
        this.rating = value;
    }

    /**
     * Gets the value of the par property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPar() {
        return par;
    }

    /**
     * Sets the value of the par property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPar(Integer value) {
        this.par = value;
    }

    /**
     * Gets the value of the yardage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getYardage() {
        return yardage;
    }

    /**
     * Sets the value of the yardage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setYardage(Integer value) {
        this.yardage = value;
    }

    /**
     * Gets the value of the gross property.
     * 
     */
    public int getGross() {
        return gross;
    }

    /**
     * Sets the value of the gross property.
     * 
     */
    public void setGross(int value) {
        this.gross = value;
    }

    /**
     * Gets the value of the esc property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getESC() {
        return esc;
    }

    /**
     * Sets the value of the esc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setESC(Integer value) {
        this.esc = value;
    }

    /**
     * Gets the value of the playType property.
     * 
     * @return
     *     possible object is
     *     {@link PlayType }
     *     
     */
    public PlayType getPlayType() {
        return playType;
    }

    /**
     * Sets the value of the playType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlayType }
     *     
     */
    public void setPlayType(PlayType value) {
        this.playType = value;
    }

    /**
     * Gets the value of the holesPlayed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHolesPlayed() {
        return holesPlayed;
    }

    /**
     * Sets the value of the holesPlayed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHolesPlayed(String value) {
        this.holesPlayed = value;
    }

    /**
     * Gets the value of the scoreType property.
     * 
     * @return
     *     possible object is
     *     {@link ScoreType }
     *     
     */
    public ScoreType getScoreType() {
        return scoreType;
    }

    /**
     * Sets the value of the scoreType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoreType }
     *     
     */
    public void setScoreType(ScoreType value) {
        this.scoreType = value;
    }

    /**
     * Gets the value of the isLocal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsLocal() {
        return isLocal;
    }

    /**
     * Sets the value of the isLocal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsLocal(Boolean value) {
        this.isLocal = value;
    }

    /**
     * Gets the value of the playedWithClubMember property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPlayedWithClubMember() {
        return playedWithClubMember;
    }

    /**
     * Sets the value of the playedWithClubMember property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPlayedWithClubMember(Boolean value) {
        this.playedWithClubMember = value;
    }

    /**
     * Gets the value of the attestor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttestor() {
        return attestor;
    }

    /**
     * Sets the value of the attestor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttestor(String value) {
        this.attestor = value;
    }

    /**
     * Gets the value of the isHoleByHole property.
     * 
     */
    public boolean isIsHoleByHole() {
        return isHoleByHole;
    }

    /**
     * Sets the value of the isHoleByHole property.
     * 
     */
    public void setIsHoleByHole(boolean value) {
        this.isHoleByHole = value;
    }

    /**
     * Gets the value of the isUsedInCalc property.
     * 
     */
    public boolean isIsUsedInCalc() {
        return isUsedInCalc;
    }

    /**
     * Sets the value of the isUsedInCalc property.
     * 
     */
    public void setIsUsedInCalc(boolean value) {
        this.isUsedInCalc = value;
    }

    /**
     * Gets the value of the holeScores property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHoleScoreInfo }
     *     
     */
    public ArrayOfHoleScoreInfo getHoleScores() {
        return holeScores;
    }

    /**
     * Sets the value of the holeScores property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHoleScoreInfo }
     *     
     */
    public void setHoleScores(ArrayOfHoleScoreInfo value) {
        this.holeScores = value;
    }

    /**
     * Gets the value of the createdOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the value of the createdOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreatedOn(XMLGregorianCalendar value) {
        this.createdOn = value;
    }

    /**
     * Gets the value of the updatedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets the value of the updatedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUpdatedOn(XMLGregorianCalendar value) {
        this.updatedOn = value;
    }

    /**
     * Gets the value of the scoreId property.
     * 
     */
    public long getScoreId() {
        return scoreId;
    }

    /**
     * Sets the value of the scoreId property.
     * 
     */
    public void setScoreId(long value) {
        this.scoreId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link ScoreStatus }
     *     
     */
    public ScoreStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoreStatus }
     *     
     */
    public void setStatus(ScoreStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link ScoreAction }
     *     
     */
    public ScoreAction getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScoreAction }
     *     
     */
    public void setAction(ScoreAction value) {
        this.action = value;
    }

    /**
     * Gets the value of the networkId property.
     * 
     */
    public int getNetworkId() {
        return networkId;
    }

    /**
     * Sets the value of the networkId property.
     * 
     */
    public void setNetworkId(int value) {
        this.networkId = value;
    }

    /**
     * Gets the value of the sourceScoreId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceScoreId() {
        return sourceScoreId;
    }

    /**
     * Sets the value of the sourceScoreId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceScoreId(String value) {
        this.sourceScoreId = value;
    }

    /**
     * Gets the value of the sourceUserId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceUserId() {
        return sourceUserId;
    }

    /**
     * Sets the value of the sourceUserId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceUserId(String value) {
        this.sourceUserId = value;
    }

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

}
