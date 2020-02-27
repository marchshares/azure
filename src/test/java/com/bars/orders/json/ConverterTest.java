package com.bars.orders.json;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class ConverterTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws UnsupportedEncodingException {
        String body = "Form=Cart&Name=Timur+Hafizov&Phone=%2B8+%28999%29+323-0393&phoneStr=79993230393&Email=marchshares%40gmail.com&deliveryType=%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D0%BE%D0%BC+%28%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%29+%3D+300&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+1&infoTheme=&deliveryClientCost=0&samovivozOrDeliveryCity=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&paymentsystem=cash&payment%5Borderid%5D=1092571402&payment%5Bproducts%5D%5B0%5D%5Bname%5D=%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D1%8B%D0%B9+%D1%82%D0%BE%D0%B2%D0%B0%D1%80&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=3000&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=3000&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=WCCN80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%A7%D0%B5%D1%80%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B1%5D%5Bname%5D=%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D1%8B%D0%B9+%D1%82%D0%BE%D0%B2%D0%B0%D1%80&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=3000&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=3000&payment%5Bproducts%5D%5B1%5D%5Bsku%5D=WCCN81&payment%5Bproducts%5D%5B1%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B1%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=10220&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=10220&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=WCCSET&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%A7%D0%B5%D1%80%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80%2BNanovessel&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=M-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D0%B4%D0%BB%D1%8F+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80%D0%B0%29&payment%5Bproducts%5D%5B3%5D%5Bname%5D=Nanopresso&payment%5Bproducts%5D%5B3%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B3%5D%5Bamount%5D=5990&payment%5Bproducts%5D%5B3%5D%5Bprice%5D=5990&payment%5Bproducts%5D%5B3%5D%5Bsku%5D=WCCN80&payment%5Bproducts%5D%5B4%5D%5Bname%5D=Nanopresso%2BNs-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B4%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B4%5D%5Bamount%5D=7190&payment%5Bproducts%5D%5B4%5D%5Bprice%5D=7190&payment%5Bproducts%5D%5B4%5D%5Bsku%5D=WCCN80%2BWCCNANS&payment%5Bproducts%5D%5B5%5D%5Bname%5D=Nanopresso+Journey+Summer+Session+%2B+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B5%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B5%5D%5Bamount%5D=6990&payment%5Bproducts%5D%5B5%5D%5Bprice%5D=6990&payment%5Bproducts%5D%5B5%5D%5Bsku%5D=WCCSMSS&payment%5Bproducts%5D%5B6%5D%5Bname%5D=Nanovessel&payment%5Bproducts%5D%5B6%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B6%5D%5Bamount%5D=1990&payment%5Bproducts%5D%5B6%5D%5Bprice%5D=1990&payment%5Bproducts%5D%5B6%5D%5Bsku%5D=WCCNNVSL&payment%5Bproducts%5D%5B7%5D%5Bname%5D=Nanopresso+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B7%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B7%5D%5Bamount%5D=1490&payment%5Bproducts%5D%5B7%5D%5Bprice%5D=1490&payment%5Bproducts%5D%5B7%5D%5Bsku%5D=WCCC_X&payment%5Bamount%5D=39870&formid=form163334058&formname=Cart&utm_source=market";
        String decodedBody = URLDecoder.decode(body, "UTF-8");

        JSONObject jsonObject = Converter.bodyLineToJsonObject(decodedBody);

        System.out.println(jsonObject.toString(1));
    }
}