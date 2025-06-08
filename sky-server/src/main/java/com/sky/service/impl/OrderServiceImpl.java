package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.properties.ShopProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/6
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private ShopProperties shopProperties;

    @Value("${sky.baidu.ak}")
    private String ak;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户提交订单
     *
     * @param ordersSubmitDTO 订单提交数据传输对象
     * @return 订单提交结果视图对象
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理各种业务异常(地址为空，购物车数据为空等)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            // 抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 检查配送范围
        checkOutOfRange(
                (addressBook.getProvinceName()==null ? "" : addressBook.getProvinceName())
                + addressBook.getCityName()
                + addressBook.getDistrictName()
                + addressBook.getDetail()
        );

            // 查询当前用户购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            // 抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orders.setAddress(addressBook.getDetail());

        orderMapper.insert(orders);

        // 向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        shoppingCartList.forEach(shoppingCart1 -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart1, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });

        orderDetailMapper.insertBatch(orderDetailList);

        // 清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        // 封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        // //调用微信支付接口，生成预支付交易单
        // JSONObject jsonObject = weChatPayUtil.pay(
        //         //商户订单号
        //         ordersPaymentDTO.getOrderNumber(),
        //         //支付金额，单位 元
        //         new BigDecimal(0.01),
        //         //商品描述
        //         "苍穹外卖订单",
        //         //微信用户的openid
        //         user.getOpenid()
        // );
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo 订单号
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        pushMessageToServer(1, orders.getId(), "订单号: " + outTradeNo);
    }

    /**
     * 订单催单
     *
     * @param id 订单ID
     */
    @Override
    public void reminderOrder(Long id) {
        // 根据id查询订单
        OrderVO orders = orderMapper.getById(id);

        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        pushMessageToServer(2, orders.getId(), orders.getNumber());

    }

    /**
     * 历史订单查询
     *
     * @param ordersPage 订单分页查询数据传输对象
     * @return 分页结果
     */
    @Override
    public PageResult conditionOrdersQuery(OrdersPageQueryDTO ordersPage) {
        // 启动分页
        PageHelper.startPage(ordersPage.getPage(), ordersPage.getPageSize());
        Page<OrderVO> page = orderMapper.getByPageQueryDTO(ordersPage);
        long total = page.getTotal();
        List<OrderVO> orderVOList = page.getResult();
        PageHelper.clearPage();
        orderVOList.forEach(orderVO -> {
             List<OrderDetail> list = orderDetailMapper.getByOrderId(orderVO.getId());
             list.forEach(orderDetail -> {
                 orderVO.setOrderDishes(orderVO.getOrderDishes() == null ? orderDetail.getName() : orderVO.getOrderDishes() + "," + orderDetail.getName());
             });
             orderVO.setOrderDetailList(list);
        });
        // 封装分页结果
        return new PageResult(total, orderVOList);
    }

    /**
     * 根据订单ID查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情视图对象
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        OrderVO orderVO = orderMapper.getById(id);
        if (orderVO == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        orderDetailList.forEach(orderDetail -> {
            orderVO.setOrderDishes(
                    orderVO.getOrderDishes() == null
                    ? orderDetail.getName()
                    : orderVO.getOrderDishes() + "," + orderDetail.getName());
        });
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 根据订单ID取消订单
     *
     * @param id 订单ID
     */
    @Override
    public void cancelOrder(Long id) {
        Orders order = Orders.builder()
                .id(id)
                .status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(order);
    }

    /**
     * 根据订单ID重新下单
     *
     * @param id 订单ID
     */
    @Override
    public void repetitionOrder(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        list.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setSetmealId(orderDetail.getSetmealId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCarts.add(shoppingCart);
        });
        if (shoppingCarts.size() > 0) {
            shoppingCartMapper.insertBatch(shoppingCarts);
        }
    }

    /**
     * 统计各状态订单数量
     *
     * @return 订单统计视图对象
     */
    @Override
    public OrderStatisticsVO getStatisticsOrders() {
        OrderStatisticsVO orderStatisticsVO = orderMapper.getStatisticsOrders();
        if (orderStatisticsVO == null) {
            orderStatisticsVO = new OrderStatisticsVO();
            orderStatisticsVO.setToBeConfirmed(0);
            orderStatisticsVO.setConfirmed(0);
            orderStatisticsVO.setDeliveryInProgress(0);
        }
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param id 订单ID
     */
    @Override
    @Transactional
    public void confirmOrders(Long id) {
        Orders orders = new Orders();
        OrderVO vo = orderMapper.getById(id);
        BeanUtils.copyProperties(vo, orders);
        // 设置订单状态为已接单
        orders.setStatus(Orders.CONFIRMED);

        orderMapper.update(orders);
    }

    /**
     * 拒单
     *
     * @param rejectionDTO 订单拒绝数据传输对象
     */
    @Override
    public void rejectionOrders(OrdersRejectionDTO rejectionDTO) {
        Orders orders = Orders.builder()
                .id(rejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(rejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        // 更新订单状态
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     *
     * @param cancelDTO 订单取消数据传输对象
     */
    @Override
    public void cancelOrder(OrdersCancelDTO cancelDTO) {
        Orders od = Orders.builder()
                .id(cancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(cancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(od);
    }

    /**
     * 派送订单
     *
     * @param id 订单ID
     */
    @Override
    public void deliveryOrder(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .deliveryStatus(1)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     */
    @Override
    public void completeOrder(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .deliveryTime(LocalDateTime.now())
                .status(Orders.COMPLETED)
                .build();
        orderMapper.update(orders);
    }

    private void checkOutOfRange(String address){
        Map<String, String> map = new HashMap<>();
        map.put("address", shopProperties.getAddress());
        map.put("output", "json");
        map.put("ak", ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        // 数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }

    }

    private void pushMessageToServer(Integer type,Long orderId, String content) {
        // 通过WebSocket向服务端推送消息  type orderId content
        Map<String, Object> map = new HashMap<>();
        // 1 表示来单提醒, 2 表示用户催单
        map.put("type", type);
        map.put("orderId", orderId);
        map.put("content", content);

        String jsonString = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

}
