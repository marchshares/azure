package com.bars.orders.functions;

import com.bars.orders.HttpResponseMessageMock;
import com.bars.orders.TestHelper;
import com.bars.orders.http.SimpleHttpClient;
import com.bars.orders.mongo.MyMongoClient;
import com.google.common.collect.Lists;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class SendSmsCdekTrackCodeFunctionTest {

    @Before
    public void setUp() {
        TestHelper.setTestProperties();
    }

    @Ignore
    @Test
    public void testFunc() {
        SendSmsCdekTrackCodeFunction func = createFunc(testJsonBody);
        HttpResponseMessage res = func.run();

        assertEquals(res.getStatus(), HttpStatus.OK);
    }

    private static SendSmsCdekTrackCodeFunction createFunc(String funcBody) {
        HttpRequestMessage request = mock(HttpRequestMessage.class);

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(request).createResponseBuilder(any(HttpStatus.class));

        final Optional<String> queryBody = Optional.of(funcBody);
        doReturn(queryBody).when(request).getBody();

        ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        SendSmsCdekTrackCodeFunction httpFunc = new SendSmsCdekTrackCodeFunction(request, context);

        return httpFunc;
    }

    private String testJsonBody = "{" +
            "\"trelloCardName\" : \"" + testTrelloCardName + "\", " +
            "\"msgTemplate\" : \"" + msgTemplate + "\", " +
            "\"trelloCardDesc\" : \"" + testTrelloCardDesc + "\"" +
            "}";

    private static final String testTrelloCardName = "1164522304: Анна Екатеринбург";

    private static final String msgTemplate = "Мы уже упаковали ваш заказ и готовим его к отправке. " +
            "Статус заказа доступен по ссылке: cdek.ru/track.html?order_id=${cdekOrderId}";

    private static final String testTrelloCardDesc = "#Заказ №1164522304 с сайта [8bars.ru]\n" +
            "\n" +
            "\n" +
            "##Информация о заказе:\n" +
            "- Wacaco Nanopresso Patrol (Артикул NPPC В наличииБренд: Wacaco, Цвет: Желтый) - 1x5200 = 5200\n" +
            "- Nanopresso NS-адаптер - 1x1400 = 1400\n" +
            "- Wacaco Nanopresso Чехол (Артикул NMCSВ наличииБренд: Wacaco, Размер: M-Чехол (для NS-адаптера)) - 1x1400 = 1400\n" +
            "\n" +
            "\n" +
            "##Информация о платеже:\n" +
            "Сумма платежа: 8000 руб\n" +
            "Платежная система: не указана\n" +
            "Время заказа: 06.01.2019 20:43\n" +
            "\n" +
            "\n" +
            "##Информация о покупателе:\n" +
            "Form: Cart\n" +
            "Name: Анна\n" +
            "Phone: +7 (900) 500-9000\n" +
            "Email: xxx@gmail.com\n" +
            "deliveryType: Доставка по России (мы свяжемся с вами для уточнение сроков и стоимости)\n" +
            "city: Екатеринбург\n" +
            "deliveryAddress: ул.Та самая д 12\n" +
            "comment: коммент\n" +
            "\n" +
            "\n" +
            "##Дополнительная информация:\n" +
            "Отправлено со страницы: https://8bars.ru/nanopresso-minipresso-case\n" +
            "Уникальный номер блока с формой: rec42581206\n" +
            "Название формы: Cart\n" +
            "Уникальный номер заявки: 514156:123191832\n" +
            "\n" +
            "\n";

}