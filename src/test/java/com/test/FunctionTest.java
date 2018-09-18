package com.test;

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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Unit test for Function class.
 */
public class FunctionTest {
    /**
     * Unit test for HttpTriggerJava method.
     */

    private final String body = "Form=Cart&Name=%D0%A2%D0%B8%D0%BC%D1%83%D1%80+%D0%A1%D0%B0%D0%B9%D1%8F%D1%80%D0%BE%D0%B2%D0%B8%D1%87+%D0%A5%D0%B0%D1%84%D0%B8%D0%B7%D0%BE%D0%B2&Phone=%2B7+%28916%29+070-9365&Email=direct-8bars%40yandex.ru&deliveryType=%D0%94%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D0%BE%D0%BC+%28%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0%2C+%D0%A1%D0%BF%D0%B1%29+300+%D1%80%D1%83%D0%B1.+%3D+300&city=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&deliveryAddress=%D0%97%D0%BE%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F+12%2C+%D0%BA%D0%BE%D1%80%D0%BF.+1%2C+%D0%BA%D0%B2.+66&comment=commnet&payment%5Bsys%5D=none&payment%5Bsystranid%5D=0&payment%5Borderid%5D=1214681071&payment%5Bproducts%5D%5B0%5D%5Bname%5D=Nanopresso+NS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80&payment%5Bproducts%5D%5B0%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B0%5D%5Bamount%5D=1300&payment%5Bproducts%5D%5B0%5D%5Bprice%5D=1300&payment%5Bproducts%5D%5B1%5D%5Bname%5D=Minipresso+GR&payment%5Bproducts%5D%5B1%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B1%5D%5Bamount%5D=4500&payment%5Bproducts%5D%5B1%5D%5Bprice%5D=4500&payment%5Bproducts%5D%5B2%5D%5Bname%5D=Nanopresso+Set&payment%5Bproducts%5D%5B2%5D%5Bquantity%5D=1&payment%5Bproducts%5D%5B2%5D%5Bamount%5D=8650&payment%5Bproducts%5D%5B2%5D%5Bprice%5D=8650&payment%5Bproducts%5D%5B2%5D%5Bsku%5D=%D0%90%D1%80%D1%82%D0%B8%D0%BA%D1%83%D0%BB+NPGS%D0%92+%D0%BD%D0%B0%D0%BB%D0%B8%D1%87%D0%B8%D0%B8%D0%91%D1%80%D0%B5%D0%BD%D0%B4%3A+Wacaco&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Boption%5D=%D0%90%D0%BA%D1%81%D0%B5%D1%81%D1%81%D1%83%D0%B0%D1%80+%28%D1%81%D0%BA%D0%B8%D0%B4%D0%BA%D0%B0+10%25%29&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bprice%5D=8650&payment%5Bproducts%5D%5B2%5D%5Boptions%5D%5B0%5D%5Bvariant%5D=Barista+Kit%2BNS-%D0%B0%D0%B4%D0%B0%D0%BF%D1%82%D0%B5%D1%80+&payment%5Bamount%5D=14750&COOKIES=ipp_uid2%3DP5mUaYZWG1rREMYM%2FagE7rK8yC7QfHrL9%2F%2F9ImQ%3D%3D%3B+ipp_uid1%3D1525430880480%3B+tildauid%3D1525430883277.471390%3B+_ga%3DGA1.2.1017251606.1525430883%3B+_ym_uid%3D152543088332406566%3B+_ym_d%3D1530655841%3B+rerf%3DAAAAAFueXUCOB82SA6GHAg%3D%3D%3B+_gid%3DGA1.2.98435159.1537105220%3B+_ym_isad%3D1%3B+_ym_visorc_44688592%3Dw%3B+_gat%3D1%3B+tildasid%3D1537111335138.878847%3B+previousUrl%3D8bars.ru%252Ftilda%252Fcart%252Fadd%252F66671186-1521897288438&formid=form42581206&formname=Cart";

    @Test
    public void testHttpTriggerJava() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.of(body);
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
    }
}
