package com.bars.orders;

import com.bars.orders.mongo.MyMongoClient;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Unit test for Function class.
 */
@Ignore
public class FunctionTest {

    public static final String TEST_URL = "https://webhook.site/d230a41e-8ea7-4bde-9a9b-5a1515ddab98";
    //public static final String TEST_URL = "https://sms.ru/sms/send";

//    private final String body = "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D0%BE%D0%BC+%28%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D0%A1%D0%BF%D0%B1%29+300+%D1%80%D1%83%D0%B1.+%3D+300&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%97%D0%BE%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F+12%2C+%D0%BA%D0%BE%D1%80%D0%BF.+1%2C+%D0%BA%D0%B2.+66&comment=%D0%BA%D0%BE%D0%BC%D0%BC%D0%B5%D0%BD%D1%82&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1557604691&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Minipresso+GR&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=4500&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+Patrol+Orange&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=5200&payment%5Bamount%5D=24450&formid=form42581206&formname=Cart";
//    private final String body = "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7+%28%D0%BC.+%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%B5%D1%86%D0%BA%D0%B0%D1%8F%29&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%A1%D0%B0%D0%BC%D0%BE%D0%B2%D1%8B%D0%B2%D0%BE%D0%B7&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1988043109&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Wacaco+Nanopresso+Tattoo&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=6500&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=6500&payment%5Bproducts%5D%5B0%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPTC+%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B0%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%96%D0%B5%D0%BB%D1%82%D1%8B%D0%B9&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=1300&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=1300&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Wacaco+Nanopresso+Patrol&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=5200&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPPC+%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%9E%D1%80%D0%B0%D0%BD%D0%B6%D0%B5%D0%B2%D1%8B%D0%B9&payment%5Bamount%5D=13000&formid=form42581206&formname=Cart&utm_source=test&utm_medium=test2";
    private final String body = "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D0%BE%D0%BC+%28%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D0%A1%D0%BF%D0%B1%29+300+%D1%80%D1%83%D0%B1.+%3D+300&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%97%D0%BE%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F+12%2C+%D0%BA%D0%BE%D1%80%D0%BF.+1%2C+%D0%BA%D0%B2.+66&comment=%D0%BA%D0%BE%D0%BC%D0%BC%D0%B5%D0%BD%D1%82&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1557604691&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Minipresso+GR&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=4500&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Nanopresso+Patrol+Orange&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=5200&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=5200&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=6650&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=6650&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPGS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80+%28%D1%81%D0%BA%D0%B8%D0%B4%D0%BA%D0%B0+10%25%29&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bprice%5D=6650&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80+&payment%5Bproducts%5D%5B3%5D%5Bname%5D=Nanopresso+Patrol+Set&payment%5Bproducts%5D%5B3%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B3%5D%5Bamount%5D=7800&payment%5Bproducts%5D%5B3%5D%5Bprice%5D=7800&payment%5Bproducts%5D%5B3%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPPS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B3%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%A6%D0%B2%D0%B5%D1%82&payment%5Bproducts%5D%5B3%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=%D0%96%D0%B5%D0%BB%D1%82%D1%8B%D0%B9&payment%5Bproducts%5D%5B3%5D%5Boptions%5D%5B1%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80+%28%D1%81%D0%BA%D0%B8%D0%B4%D0%BA%D0%B0+10%25%29&payment%5Bproducts%5D%5B3%5D%5Boptions%5D%5B1%5D%5Bprice%5D=7800&payment%5Bproducts%5D%5B3%5D%5Boptions%5D%5B1%5D%5Bvariant%5D=Barista+Kit%2BNS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80+&payment%5Bamount%5D=24450&COOKIES=ipp_uid2%3DP5mUaYZWG1rREMYM%2FagE7rK8yC7QfHrL9%2F%2F9ImQ%3D%3D%3B+ipp_uid1%3D1525430880480%3B+tildauid%3D1525430883277.471390%3B+_ga%3DGA1.2.1017251606.1525430883%3B+_ym_uid%3D152543088332406566%3B+_ym_d%3D1530655841%3B+rerf%3DAAAAAFueXUCOB82SA6GHAg%3D%3D%3B+_ym_visorc_44688592%3Dw%3B+tildasid%3D1537301508004.319282%3B+_gid%3DGA1.2.2106621781.1537301508%3B+_ym_isad%3D2%3B+_gat%3D1%3B+previousUrl%3D8bars.ru%252Ftilda%252Fcart%252Fadd%252F66671186-1536498211622&formid=form42581206&formname=Cart";

    @Test
    public void testHttpTriggerJava() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.of(body);
        doReturn(queryBody).when(req).getBody();

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        MyMongoClient mongoClient = mock(MyMongoClient.class);
        when(mongoClient.getOrderIds()).thenReturn(Lists.newArrayList());

        // Invoke
        Function httpFunc = new Function(req, context);
        httpFunc.setZapierProductsUrl(TEST_URL);
        httpFunc.setMyMongoClient(mongoClient);

        httpFunc.init();

        final HttpResponseMessage res = httpFunc.run();

        // Verify
        assertEquals(res.getStatus(), HttpStatus.OK);
        assertEquals(httpFunc.getOrder().getProducts().size(), 7);
    }
}
