
package eu.openminted.registry.domain;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mailingListInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mailingListInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mailingListName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="subscribe" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}emailAddress"/&gt;
 *         &lt;element name="unsubscribe" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}emailAddress"/&gt;
 *         &lt;element name="post" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}emailAddress"/&gt;
 *         &lt;element name="archive" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}httpURI"/&gt;
 *         &lt;element name="otherArchives" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="otherArchive" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}httpURI" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mailingListInfoType", propOrder = {
    "mailingListName",
    "subscribe",
    "unsubscribe",
    "post",
    "archive",
    "otherArchives"
})
public class MailingListInfo {

    @XmlElement(required = true)
    protected String mailingListName;
    @XmlElement(required = true)
    protected String subscribe;
    @XmlElement(required = true)
    protected String unsubscribe;
    @XmlElement(required = true)
    protected String post;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String archive;
    @XmlElementWrapper
    @XmlElement(name = "otherArchive", namespace = "http://www.meta-share.org/OMTD-SHARE_XMLSchema")
    protected List<String> otherArchives;

    /**
     * Gets the value of the mailingListName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailingListName() {
        return mailingListName;
    }

    /**
     * Sets the value of the mailingListName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailingListName(String value) {
        this.mailingListName = value;
    }

    /**
     * Gets the value of the subscribe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscribe() {
        return subscribe;
    }

    /**
     * Sets the value of the subscribe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscribe(String value) {
        this.subscribe = value;
    }

    /**
     * Gets the value of the unsubscribe property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnsubscribe() {
        return unsubscribe;
    }

    /**
     * Sets the value of the unsubscribe property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnsubscribe(String value) {
        this.unsubscribe = value;
    }

    /**
     * Gets the value of the post property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPost() {
        return post;
    }

    /**
     * Sets the value of the post property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPost(String value) {
        this.post = value;
    }

    /**
     * Gets the value of the archive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArchive() {
        return archive;
    }

    /**
     * Sets the value of the archive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArchive(String value) {
        this.archive = value;
    }

    public List<String> getOtherArchives() {
        if (otherArchives == null) {
            otherArchives = new ArrayList<String>();
        }
        return otherArchives;
    }

    public void setOtherArchives(List<String> otherArchives) {
        this.otherArchives = otherArchives;
    }

}
