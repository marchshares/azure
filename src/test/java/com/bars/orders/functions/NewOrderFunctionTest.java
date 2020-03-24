package com.bars.orders.functions;

import com.bars.orders.TestHelper;
import com.bars.orders.http.ZapierHttpClient;
import com.bars.orders.order.Order;
import com.bars.orders.product.Product;
import com.bars.orders.mongo.MyMongoClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.azure.functions.*;

import java.util.*;

import static com.bars.orders.TestHelper.invokeRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Unit test for NewOrderFunction class.
 */
public class NewOrderFunctionTest {
    private static final Set<String> possibleNames = Sets.newHashSet(
            "Minipresso GR","Minipresso NS",
            "Nanopresso","Nanopresso Patrol Yellow","Nanopresso Patrol Orange","Nanopresso Patrol Red",
            "Barista Kit","NS-адаптер",
            "MP-Чехол","S-Чехол","M-Чехол","L-Чехол",
            "Nanovessel",
            "Nanopresso Tattoo Yellow","Nanopresso Tattoo Orange",
            "Nanopresso Elements Chill White","Nanopresso Elements Moss Green","Nanopresso Elements Arctic Blue","Nanopresso Elements Lava Red",
            "Nanopresso+NS-адаптер",
            "Minipresso Tank","Minipresso Kit",
            "Nanopresso Journey Winter Ride","Nanopresso Journey Spring Run","Nanopresso Journey Summer Session","Nanopresso Journey Fall Break",
            "Nanopresso Чехол"

    );
    private String testOrderId = "1111111111";
    String testBody = "Form=Cart&Name=Timur+Hafizov&Phone=%2B8+%28915%29+323-0393&phoneStr=79153230393&comment=no&Email=marchshares%40gmail.com&DeliveryType=%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D0%BE%D0%BC+%28%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%29+%3D+300&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&DeliveryAddress=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+1&infoTheme=&DeliveryClientCost=0&samovivozOrDeliveryCity=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&paymentsystem=cash&payment%5Borderid%5D=1111111111&payment%5Bproducts%5D%5B0%5D%5Bname%5D=%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D1%8B%D0%B9+%D1%82%D0%BE%D0%B2%D0%B0%D1%80&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=3000&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=3000&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=WCCN80&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%A7%D0%B5%D1%80%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B1%5D%5Bname%5D=%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D1%8B%D0%B9+%D1%82%D0%BE%D0%B2%D0%B0%D1%80&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=3000&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=3000&payment%5Bproducts%5D%5B1%5D%5Bsku%5D=WCCN81&payment%5Bproducts%5D%5B1%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B1%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=10220&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=10220&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=WCCSET&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%A7%D0%B5%D1%80%D0%BD%D1%8B%D0%B9&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80%2BNanovessel&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Boption%5D=%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B2%5D%5Bvariant%5D=M-%D0%A7%D0%B5%D1%85%D0%BE%D0%BB+%28%D0%B4%D0%BB%D1%8F+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80%D0%B0%29&payment%5Bproducts%5D%5B3%5D%5Bname%5D=Nanopresso&payment%5Bproducts%5D%5B3%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B3%5D%5Bamount%5D=5990&payment%5Bproducts%5D%5B3%5D%5Bprice%5D=5990&payment%5Bproducts%5D%5B3%5D%5Bsku%5D=WCCN80&payment%5Bproducts%5D%5B4%5D%5Bname%5D=Nanopresso%2BNs-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B4%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B4%5D%5Bamount%5D=7190&payment%5Bproducts%5D%5B4%5D%5Bprice%5D=7190&payment%5Bproducts%5D%5B4%5D%5Bsku%5D=WCCN80%2BWCCNANS&payment%5Bproducts%5D%5B5%5D%5Bname%5D=Nanopresso+Journey+Summer+Session+%2B+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B5%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B5%5D%5Bamount%5D=6990&payment%5Bproducts%5D%5B5%5D%5Bprice%5D=6990&payment%5Bproducts%5D%5B5%5D%5Bsku%5D=WCCSMSS&payment%5Bproducts%5D%5B6%5D%5Bname%5D=Nanovessel&payment%5Bproducts%5D%5B6%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B6%5D%5Bamount%5D=1990&payment%5Bproducts%5D%5B6%5D%5Bprice%5D=1990&payment%5Bproducts%5D%5B6%5D%5Bsku%5D=WCCNNVSL&payment%5Bproducts%5D%5B7%5D%5Bname%5D=Nanopresso+%D0%A7%D0%B5%D1%85%D0%BE%D0%BB&payment%5Bproducts%5D%5B7%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B7%5D%5Bamount%5D=1490&payment%5Bproducts%5D%5B7%5D%5Bprice%5D=1490&payment%5Bproducts%5D%5B7%5D%5Bsku%5D=WCCC_X&payment%5Bamount%5D=39870&formid=form163334058&formname=Cart&utm_source=market";

    private List<TestBody> testBodies = Lists.newArrayList(
    );

    @Before
    public void setUp() {
        TestHelper.setTestProperties();
    }

    @Test
    @Ignore
    public void testWebhook() throws Exception {
        NewOrderFunction httpFunc = createFunc(testBody);
        httpFunc.init();

        httpFunc.getMyMongoClient().removeOrder(testOrderId);

        final HttpResponseMessage res = httpFunc.run();

        assertEquals(res.getStatus(), HttpStatus.OK);
        System.out.println("\n------ Result --------");
        System.out.println("Count of products: " + httpFunc.getOrder().getProducts().size());
        httpFunc.getOrder().getProducts().forEach(product -> {
            System.out.println(product.getName() + ": " + possibleNames.contains(product.getName()));
        });
    }

    @Test
    public void testSplitting0() throws Exception {
        ArrayList<String> names = Lists.newArrayList(
            "Nanopresso",

            "Nanopresso Patrol Red",

            "Nanopresso",
            "NS-адаптер",
            "Nanovessel",
            "M-Чехол",

            "Nanopresso",

            "Nanopresso",
            "NS-адаптер",

            "Nanopresso Journey Summer Session",

            "Nanovessel",

            "Nanopresso Чехол"
        );

        System.out.println("\n-----------------");
        NewOrderFunction httpFunc = createFunc(testBody, true, true);
        httpFunc.init();

        final HttpResponseMessage res = httpFunc.run();


        // Verify
        assertEquals(res.getStatus(), HttpStatus.OK);
        httpFunc.getOrder().getProducts()
                .stream()
                .map(Product::getName)
                .forEach(name -> {
                    boolean removed = names.remove(name);
                    assertTrue("'" + name + "' not found", removed);
                });

        assertTrue(names.isEmpty());
    }

    @Test
    public void testSplitting() throws Exception {
        int i = 1;
        for (TestBody testBody : testBodies) {
            System.out.println("\n-----------------");
            System.out.println("Num: " + i++);
            NewOrderFunction httpFunc = createFunc(testBody.body, true, true);
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
        return createFunc(funcBody, false, false);
    }

    private static NewOrderFunction createFunc(String funcBody, boolean mockHttp, boolean mockMongo) {
        NewOrderFunction httpFunc = new NewOrderFunction(invokeRequest(funcBody));

        if (mockHttp) {
            ZapierHttpClient zapierHttpClient = mock(ZapierHttpClient.class);

            doNothing().when(zapierHttpClient).sendOrderToZapier(any(Order.class));
            httpFunc.setZapierHttpClient(zapierHttpClient);
        }

        if (mockMongo) {
            MyMongoClient mongoClient = mock(MyMongoClient.class);

            when(mongoClient.getOrderIds()).thenReturn(Lists.newArrayList());
            httpFunc.setMyMongoClient(mongoClient);
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
