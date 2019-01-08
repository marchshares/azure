package com.bars.orders.functions;

import com.bars.orders.HttpResponseMessageMock;
import com.bars.orders.TestHelper;
import com.bars.orders.functions.NewOrderFunction;
import com.bars.orders.http.SimpleHttpClient;
import com.bars.orders.json.Product;
import com.bars.orders.mongo.MyMongoClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.azure.functions.*;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Unit test for NewOrderFunction class.
 */
@Ignore
public class NewOrderFunctionTest {
    private static final Set<String> possibleNames = Sets.newHashSet(
            "Nanopresso Tattoo Orange", "Nanopresso Tattoo Yellow",
            "Nanopresso", "Minipresso GR", "Minipresso NS",
            "Nanopresso Patrol Yellow", "Nanopresso Patrol Orange", "Nanopresso Patrol Red",
            "NS-адаптер", "Barista Kit",
            "M-Чехол", "S-Чехол", "L-Чехол"

    );
    private String testBody = "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1448165825&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Wacaco+Nanopresso+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NMCS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A0%D0%B0%D0%B7%D0%BC%D0%B5%D1%80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=S-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D1%8B%D0%B9%29&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+M-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=5940&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=5940&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=%D0%9A%D0%BE%D0%BC%D0%BF%D0%BB%D0%B5%D0%BA%D1%82+%D0%BD%D0%B0+%D0%B2%D1%81%D0%B5+%D1%81%D0%BB%D1%83%D1%87%D0%B0%D0%B8+%D0%B6%D0%B8%D0%B7%D0%BD%D0%B8&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%96%D0%B5%D0%BB%D1%82%D1%8B%D0%B9&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=%D0%9D%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Bprice%5D=1260&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=L-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D0%B4%D0%BB%D1%8F+Barista+Kit%29&payment%5Bamount%5D=8740&formid=form42581206&formname=Cart";

    private List<TestBody> testBodies = Lists.newArrayList(
            new TestBody(2, "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D0%BE%D0%BC+%28%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D0%A1%D0%BF%D0%B1%29+300+%D1%80%D1%83%D0%B1.+%3D+300&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%97%D0%BE%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F+12%2C+%D0%BA%D0%BE%D1%80%D0%BF.+1%2C+%D0%BA%D0%B2.+66&comment=%D0%BA%D0%BE%D0%BC%D0%BC%D0%B5%D0%BD%D1%82&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1557604691&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Minipresso+GR&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=4500&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+Patrol+Orange&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=5200&payment%5Bamount%5D=24450&formid=form42581206&formname=Cart"),
            new TestBody(3, "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1988043109&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Wacaco+Nanopresso+Tattoo&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=6500&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=6500&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPTC+%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%96%D0%B5%D0%BB%D1%82%D1%8B%D0%B9&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=1300&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=1300&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Wacaco+Nanopresso+Patrol&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=5200&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPPC+%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%9E%D1%80%D0%B0%D0%BD%D0%B6%D0%B5%D0%B2%D1%8B%D0%B9&payment%5Bamount%5D=13000&formid=form42581206&formname=Cart&utm_source=test&utm_medium=test2"),
            new TestBody(4, "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1663270897&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=9270&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=9270&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=%D0%9A%D0%BE%D0%BC%D0%BF%D0%BB%D0%B5%D0%BA%D1%82+%D0%BD%D0%B0+%D0%B2%D1%81%D0%B5+%D1%81%D0%BB%D1%83%D1%87%D0%B0%D0%B8+%D0%B6%D0%B8%D0%B7%D0%BD%D0%B8&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%A7%D0%B5%D1%80%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B1%5D%5Bprice%5D=8010&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=Barista+Kit%2BNS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B2%5D%5Bprice%5D=1260&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=M-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D0%B4%D0%BB%D1%8F+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80%D0%B0%29&payment%5Bamount%5D=9270&formid=form42581206&formname=Cart"),
            new TestBody(1, "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1184750903&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Wacaco+Nanopresso+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NMCS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A0%D0%B0%D0%B7%D0%BC%D0%B5%D1%80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=S-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D1%8B%D0%B9%29&payment%5Bamount%5D=1400&formid=form42581206&formname=Cart"),
            new TestBody(4, "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1448165825&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Wacaco+Nanopresso+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NMCS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A0%D0%B0%D0%B7%D0%BC%D0%B5%D1%80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=S-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D1%8B%D0%B9%29&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+M-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=5940&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=5940&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=%D0%9A%D0%BE%D0%BC%D0%BF%D0%BB%D0%B5%D0%BA%D1%82+%D0%BD%D0%B0+%D0%B2%D1%81%D0%B5+%D1%81%D0%BB%D1%83%D1%87%D0%B0%D0%B8+%D0%B6%D0%B8%D0%B7%D0%BD%D0%B8&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%96%D0%B5%D0%BB%D1%82%D1%8B%D0%B9&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=%D0%9D%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Bprice%5D=1260&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=L-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D0%B4%D0%BB%D1%8F+Barista+Kit%29&payment%5Bamount%5D=8740&formid=form42581206&formname=Cart"),
            new TestBody(14, "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1959512504&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Minipresso+GR&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=4500&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Wacaco+Minipresso+NS&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+MPNS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=5200&payment%5Bproducts%5D%5B3%5D%5Bname%5D=Nanopresso+Patrol+Red&payment%5Bproducts%5D%5B3%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B3%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B3%5D%5Bprice%5D=5200&payment%5Bproducts%5D%5B4%5D%5Bname%5D=Wacaco+Nanopresso+Patrol&payment%5Bproducts%5D%5B4%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B4%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B4%5D%5Bprice%5D=5200&payment%5Bproducts%5D%5B4%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPPC+%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B4%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B4%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B5%5D%5Bname%5D=Wacaco+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80+%D0%B4%D0%BB%D1%8F+Nanopresso&payment%5Bproducts%5D%5B5%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B5%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B5%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B5%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NSAD%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B6%5D%5Bname%5D=Nanopresso+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B6%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B6%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B6%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B7%5D%5Bname%5D=Nanopresso+L-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B7%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B7%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B7%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B8%5D%5Bname%5D=Wacaco+Nanopresso+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B8%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B8%5D%5Bamount%5D=1400&payment%5Bproducts%5D%5B8%5D%5Bprice%5D=1400&payment%5Bproducts%5D%5B8%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NMCS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B8%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A0%D0%B0%D0%B7%D0%BC%D0%B5%D1%80&payment%5Bproducts%5D%5B8%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=S-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D1%81%D1%82%D0%B0%D0%BD%D0%B4%D0%B0%D1%80%D1%82%D0%BD%D1%8B%D0%B9%29&payment%5Bproducts%5D%5B9%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B9%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B9%5D%5Bamount%5D=5940&payment%5Bproducts%5D%5B9%5D%5Bprice%5D=5940&payment%5Bproducts%5D%5B9%5D%5Bsku%5D=%D0%9A%D0%BE%D0%BC%D0%BF%D0%BB%D0%B5%D0%BA%D1%82+%D0%BD%D0%B0+%D0%B2%D1%81%D0%B5+%D1%81%D0%BB%D1%83%D1%87%D0%B0%D0%B8+%D0%B6%D0%B8%D0%B7%D0%BD%D0%B8&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%96%D0%B5%D0%BB%D1%82%D1%8B%D0%B9&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=%D0%9D%D0%B5%D1%82&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B2%5D%5Bprice%5D=1260&payment%5Bproducts%5D%5B9%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=M-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D0%B4%D0%BB%D1%8F+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80%D0%B0%29&payment%5Bproducts%5D%5B10%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B10%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B10%5D%5Bamount%5D=8010&payment%5Bproducts%5D%5B10%5D%5Bprice%5D=8010&payment%5Bproducts%5D%5B10%5D%5Bsku%5D=%D0%9A%D0%BE%D0%BC%D0%BF%D0%BB%D0%B5%D0%BA%D1%82+%D0%BD%D0%B0+%D0%B2%D1%81%D0%B5+%D1%81%D0%BB%D1%83%D1%87%D0%B0%D0%B8+%D0%B6%D0%B8%D0%B7%D0%BD%D0%B8&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%9E%D1%80%D0%B0%D0%BD%D0%B6%D0%B5%D0%B2%D1%8B%D0%B9&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B1%5D%5Bprice%5D=8010&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=Barista+Kit%2BNS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B10%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=%D0%9D%D0%B5%D1%82&payment%5Bamount%5D=44150&formid=form42581206&formname=Cart")
    );

    @Before
    public void setUp() {
        TestHelper.setTestProperties();
    }

    @Test
    @Ignore
    public void testWebhook() {
        NewOrderFunction httpFunc = createFunc(testBody);
        httpFunc.init();
        final HttpResponseMessage res = httpFunc.run();

        assertEquals(res.getStatus(), HttpStatus.OK);
        System.out.println("\n------ Result --------");
        System.out.println("Count of products: " + httpFunc.getOrder().getProducts().size());
        httpFunc.getOrder().getProducts().forEach(product -> {
            System.out.println(product.getName() + ": " + possibleNames.contains(product.getName()));
        });
    }

    @Test
    public void testSplitting() {
        int i = 1;
        for (TestBody testBody : testBodies) {
            System.out.println("\n-----------------");
            System.out.println("Num: " + i++);
            NewOrderFunction httpFunc = createFunc(testBody.body, true);
            httpFunc.init();

            final HttpResponseMessage res = httpFunc.run();

            // Verify
            assertEquals(res.getStatus(), HttpStatus.OK);
            assertEquals(testBody.countOfProducts, httpFunc.getOrder().getProducts().size());
            httpFunc.getOrder().getProducts()
                    .stream()
                    .map(Product::getName)
                    .forEach(name -> {
                        assertTrue("'" + name + "' not found", possibleNames.contains(name));
                    });
        }
    }

    private static NewOrderFunction createFunc(String funcBody) {
        return createFunc(funcBody, false);
    }

    private static NewOrderFunction createFunc(String funcBody, boolean mockHttp) {
        HttpRequestMessage request = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        doReturn(queryParams).when(request).getQueryParameters();

        final Optional<String> queryBody = Optional.of(funcBody);
        doReturn(queryBody).when(request).getBody();

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(request).createResponseBuilder(any(HttpStatus.class));

        ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        MyMongoClient mongoClient = mock(MyMongoClient.class);
        when(mongoClient.getOrderIds()).thenReturn(Lists.newArrayList());

        // Invoke
        NewOrderFunction httpFunc = new NewOrderFunction(request, context);
        httpFunc.setMyMongoClient(mongoClient);

        if (mockHttp) {
            SimpleHttpClient httpClient = mock(SimpleHttpClient.class);

            doNothing().when(httpClient).sendZapier(anyString());
            httpFunc.setHttpClient(httpClient);
        }

        return httpFunc;
    }

    public class TestBody {
        int countOfProducts;
        String body;

        public TestBody(int countOfProducts, String body) {
            this.countOfProducts = countOfProducts;
            this.body = body;
        }
    }
}
