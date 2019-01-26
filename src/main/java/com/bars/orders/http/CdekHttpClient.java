package com.bars.orders.http;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import static com.bars.orders.PropertiesHelper.getSystemProp;

public class CdekHttpClient extends SimpleHttpClient {
    public static final String CDEK_ORDER_ID_PATTERN = "^[0-9]{3,20}$";

    private final String cdekBaseUrl;
    private final String cdekAccount;
    private final String cdekSecure;

    private static final String STATUS_REPORT_URL = "status_report_h.php";

    private static final SimpleDateFormat dateXmlFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public CdekHttpClient(Logger logger) {
        super(logger);

        this.cdekBaseUrl = getSystemProp("CdekBaseUrl");
        this.cdekAccount = getSystemProp("CdekAccount");
        this.cdekSecure = getSystemProp("CdekSecure");

        this.requestContentType = "application/x-www-form-urlencoded";

        resolveRequestXmls();
    }

    public String getCdekOrderId(String paymentId) {
        Document document = sendStatusReportForOne(paymentId);

        NodeList orders = document.getElementsByTagName("Order");
        if (orders.getLength() == 0) {
            throw new RuntimeException("paymentId=" + paymentId + " not found't in CDEK response: " + document);
        }

        NamedNodeMap firstAttributes = orders
                .item(0)
                .getAttributes();

        Node errorCode = firstAttributes.getNamedItem("ErrorCode");
        if (errorCode != null) {
            throw new RuntimeException("paymentId=" + paymentId + " returned with error in CDEK response: " +
                    "ErrorCode: "+ errorCode.getNodeValue()+ ", Msg: " + firstAttributes.getNamedItem("Msg").getNodeValue());
        }

        String number = firstAttributes.getNamedItem("Number").getNodeValue();
        if (! paymentId.equals(number)) {
            throw new RuntimeException("paymentId=" + paymentId + " not equals Number in CDEK response: " + number);
        }

        String cdekOrderId = firstAttributes.getNamedItem("DispatchNumber").getNodeValue();

        if (! cdekOrderId.matches(CDEK_ORDER_ID_PATTERN)) {
            throw new RuntimeException("cdekOrderId=" + cdekOrderId + "not in pattern=" + CDEK_ORDER_ID_PATTERN);
        }

        logger.info("cdekOrderId:" + cdekOrderId);

        return cdekOrderId;
    }

    public Document sendStatusReportForOne(String paymentId) {
        String finalUrl = cdekBaseUrl + "/" + STATUS_REPORT_URL;

        Date dateFrom = getDateFrom();
        String body = statusReportRequestXml
                .replace("{paymentId}",paymentId)
                .replace("{dateFrom}",dateXmlFormatter.format(dateFrom));

        SimpleHttpResponse response = sendPost(finalUrl, body);

        try {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response.content)));

        } catch (Exception e) {
            throw new RuntimeException("Couldn't parse CDEK response: " + response, e);
        }
    }

    private Date getDateFrom() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);

        return cal.getTime();
    }

    private void resolveRequestXmls() {

        statusReportRequestXml = statusReportRequestXml
                .replace("{todayDate}", dateXmlFormatter.format(new Date()))
                .replace("{account}",cdekAccount)
                .replace("{secure}",cdekSecure);
    }

    private String statusReportRequestXml = "xml_request=" +
            "<StatusReport " +
            "   Date=\"{todayDate}\" " +
            "   Account=\"{account}\" " +
            "   Secure=\"{secure}\" " +
            "   ShowHistory=\"0\">\n" +
            " " +
            "   <Order Number=\"{paymentId}\" DateFirst=\"{dateFrom}\"/>" +
            " " +
            "</StatusReport>";
}
