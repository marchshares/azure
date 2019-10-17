package com.bars.orders.json;

import com.bars.orders.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class OrderTest {

    private static final int MIN_ORDER_ID = 1_000_000_000;

    @Before
    public void setUp() throws Exception {
        TestHelper.setTestProperties();
    }

    @Test
    public void shouldCreateOrder() {
        Order testOrder = createTestOrder();
        System.out.println(testOrder.getOrderId());
    }

    public static Order createTestOrder() {
        String decodedBody = "Form=Cart&Name=Имя Фамилия&Phone=+7 (999) 111-2233&Email=email@yandex.ru&deliveryType=Доставка курьером (Москва, Спб) 300 руб. = 300&city=Москва&deliveryAddress=адрес такой-то&comment=коммент&payment[sys]=none&payment[systranid]=0&payment[orderid]=1557604691&payment[products][0][name]=Minipresso GR&payment[products][0][quantity]=1&payment[products][0][amount]=4500&payment[products][0][price]=4500&payment[products][1][name]=Nanopresso Patrol Orange&payment[products][1][quantity]=1&payment[products][1][amount]=5200&payment[products][1][price]=5200&payment[amount]=24450&formid=form42581206&formname=Cart";

        Order testOrder = new Order(decodedBody);

        String newOrderId = Long.toString(MIN_ORDER_ID + Math.abs(new Random().nextInt(MIN_ORDER_ID)));

        testOrder.setOrderId(newOrderId);

        return testOrder;
    }
}