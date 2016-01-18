package com.supf.tptest;

import com.alibaba.cainiao.cndcp.jz.JzServiceTypeServiceEnum;
import com.alibaba.common.lang.StringUtil;
import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.standalone.HSFEasyStarter;
import com.taobao.tc.domain.dataobject.BizOrderDO;
import com.taobao.tc.domain.result.SingleQueryResultDO;
import com.taobao.tc.service.TcBaseService;
import com.taobao.trade.test.DefaultTpTestClient;
import com.taobao.trade.test.domain.TcBizOrderFieldEnum;
import com.taobao.trade.test.domain.TcLogisticOrderFieldEnum;
import com.taobao.trade.test.param.*;
import com.taobao.trade.test.request.OrderQueryRequest;
import com.taobao.trade.test.request.PayRequest;
import com.taobao.trade.test.request.UpdateAttrubuteRequest;
import com.taobao.trade.test.request.UpdateFieldRequest;
import com.taobao.trade.test.response.BizOrder;
import com.taobao.trade.test.response.CreateResponse;
import com.taobao.trade.test.response.OrderQueryResponse;
import com.taobao.wlb.res.client.readservice.ResourceReadService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明
 *
 * @author <a href="mailto:pengfei.spf@alibaba-inc.com">pengfei.spf</a>
 * @version 1.0
 * @since 2016/1/5 12:39
 */
public class JiyouJiaTpTest1 {


    public static void main(String[] args) throws Exception {
        HSFEasyStarter.start("d:/taobao-hsf/", "");
        HSFSpringConsumerBean consumerBean = new HSFSpringConsumerBean();
        consumerBean.setVersion("1.0.0.daily");
        consumerBean.setInterfaceName("com.taobao.tc.service.TcBaseService");
        consumerBean.init();
        TcBaseService tcBaseService =   (TcBaseService)consumerBean.getObject();
        /**家具*/
        JzServiceTypeServiceEnum serviceEnum = JzServiceTypeServiceEnum.SERVICE_TMS_SEND_STATION;
//        String sellerId = "183494533";//auctiondetail5
        String sellerId =  "3646216870";//shileb103
        DefaultTpTestClient defaultTpTestClient = new DefaultTpTestClient();
        // 创建主订单
        com.taobao.trade.test.request.CreateRequest createRequest = new com.taobao.trade.test.request.CreateRequest();
        CreateParam createParam = new CreateParam();
        createParam.setMain(true);
        Map<String, String> att1Map = new HashMap<String, String>();
        createParam.setBizOrderattributes(att1Map);
        createParam.setBuyerid(175757425l);

        // 创建虚拟子订单[安裝服务]
        CreateParam createParam1 = new CreateParam();
        Map<String, String> stringMap = new HashMap<String, String>();
        stringMap.put("isB2C", "1");
        stringMap.put("jiajufuwu", "JIAJU_FUWU_ANZHUANG");
        stringMap.put("extService", "furniture");
        stringMap.put(TcBizOrderFieldEnum.auctionTitle.getField(), "安装");
        createParam1.setBizOrderattributes(stringMap);

        CreateParam createParam2 = new CreateParam();

        Map<String, String> valMap2 = new HashMap<String, String>();
        valMap2.put("isB2C", "1");
        valMap2.put("jiajufuwu", "JIAJU_FUWU_PEISONG");
        valMap2.put("extService", "furniture");
        valMap2.put(TcBizOrderFieldEnum.auctionTitle.getField(), "配送");
        createParam2.setBizOrderattributes(valMap2);

        CreateParam createParam3 = new CreateParam();
        Map<String, String> valMap3 = new HashMap<String, String>();
        valMap3.put("isB2C", "1");
        valMap3.put("jiajufuwu", "JIAJU_FUWU_BANLOU");
        valMap3.put(TcBizOrderFieldEnum.auctionTitle.getField(), "搬楼");
        valMap3.put("extService", "furniture");
        createParam3.setBizOrderattributes(valMap3);


        // 创建实物子订单
        CreateParam goodsParam = new CreateParam();
        Map<String, String> attMap = new HashMap<String, String>();
        attMap.put("serid", "1");
        attMap.put("isB2C", "1");
        attMap.put("itemTag", "35265,76162");
        attMap.put("jiajufuwu", "JIAJU_FUWU_DELIVERY");
        attMap.put("categoryId", "50008164");
        attMap.put(TcBizOrderFieldEnum.auctionTitle.getField(), "地板");
        goodsParam.setBizOrderattributes(attMap);


        List<CreateParam> list = new ArrayList<CreateParam>();
        list.add(createParam1);
        list.add(createParam2);
        list.add(createParam3);
        list.add(goodsParam);
        createParam.setDetailOrders(list);

        createParam.setTostat(CreateParam.STAT_CREATE_ORDER_WITH_CREATE_ALIPAY);
        createRequest.setCreateParam(createParam);
        CreateResponse createResponse = defaultTpTestClient.execute(createRequest);
        System.out.println(createResponse.getBizOrderid());

        // 查询订单细腻
        OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
        OrderQueryParam orderQueryParam = new OrderQueryParam();
        orderQueryParam.setBizOrderId(createResponse.getBizOrderid());
        orderQueryParam.setShowLogisticOrder(true);
        orderQueryParam.setShowPayOrder(true);
        orderQueryRequest.setOrderQueryParam(orderQueryParam);
        OrderQueryResponse orderQueryResponse = defaultTpTestClient.execute(orderQueryRequest);

        // 修改主订单
        UpdateFieldRequest mainFieldRequest = new UpdateFieldRequest();
        UpdateFieldParam mainFieldParam = new UpdateFieldParam();
        Map<String, String> upMap = new HashMap<String, String>();
        upMap.put(TcBizOrderFieldEnum.sellerId.getField(), sellerId);
        mainFieldParam.setBizOrderFields(upMap);
        mainFieldParam.setBizOrderId(createResponse.getBizOrderid());
        mainFieldRequest.setUpdateFieldParam(mainFieldParam);
        defaultTpTestClient.execute(mainFieldRequest);
        SingleQueryResultDO resultDO = tcBaseService.getBizOrderById(createResponse.getBizOrderid());

        // 修改实物订单
        int i = 0;
        for(BizOrderDO bizOrder : resultDO.getBizOrder().getDetailOrderList()){
            Map<String, String> oMap = new HashMap<String, String>();
            oMap.put(TcBizOrderFieldEnum.sellerId.getField(), sellerId);
            if(StringUtil.isNotBlank(bizOrder.getAttributes().get("serid"))){
                oMap.put(TcBizOrderFieldEnum.outOrderId.getField(), orderQueryResponse.getBizOrder().getOutOrderId() + "_1");
                oMap.put(TcBizOrderFieldEnum.auctionTitle.getField(), "极有家");
                //修改实物子订单垂直表属性
                Map<String, String> verticalStringMap = new HashMap<String, String>();
                verticalStringMap.put("service_tags", "TMALL_MARKET$mcTime@960&esTime@1&ssid@5000000011414_502&cutTime@16&storeCode@ALOG-001&lgType@5&");
                Map<Integer, Map<String, String>> verticalMap2 = new HashMap<Integer, Map<String, String>>();
                verticalMap2.put(1, verticalStringMap);
                UpdateAttributeParam updateAttributeParam2 = new UpdateAttributeParam();
                updateAttributeParam2.setTableType(5);
                updateAttributeParam2.setNeedUpdateVerticlAttributes(verticalMap2);
                updateAttributeParam2.setBizOrderId(bizOrder.getBizOrderId());
                UpdateAttrubuteRequest updateAttributeRequest2 = new UpdateAttrubuteRequest();
                updateAttributeRequest2.setUpdateAttributeParam(updateAttributeParam2);
                System.out.println(defaultTpTestClient.execute(updateAttributeRequest2).isSuccess());
            }
            else{
                oMap.put(TcBizOrderFieldEnum.options.getField(), "4");
                oMap.put(TcBizOrderFieldEnum.outOrderId.getField(), orderQueryResponse.getBizOrder().getOutOrderId() + "_1_"+i++);
                oMap.put(TcBizOrderFieldEnum.auctionTitle.getField(), bizOrder.getAttribute(TcBizOrderFieldEnum.auctionTitle.getField()));
            }
            UpdateFieldRequest oRequest = new UpdateFieldRequest();
            UpdateFieldParam oParam = new UpdateFieldParam();
            oParam.setBizOrderFields(oMap);
            oParam.setBizOrderId(bizOrder.getBizOrderId());
            oRequest.setUpdateFieldParam(oParam);
            defaultTpTestClient.execute(oRequest);
        }

        // 修改物流订单

        Map<String, String> shipMap = new HashMap<String, String>();
//        shipMap.put(TcLogisticOrderFieldEnum.prov.getField(), "浙江省");
//        shipMap.put(TcLogisticOrderFieldEnum.city.getField(), "杭州市");
//        shipMap.put(TcLogisticOrderFieldEnum.area.getField(), "西湖区");
//        shipMap.put(TcLogisticOrderFieldEnum.town.getField(), "古荡街道");
//        shipMap.put(TcLogisticOrderFieldEnum.divisionCode.getField(), "330102");
        shipMap.put(TcLogisticOrderFieldEnum.divisionCode.getField(), "330106008");
        shipMap.put(TcLogisticOrderFieldEnum.address.getField(), "我是详细地址");
        shipMap.put(TcLogisticOrderFieldEnum.mobilePhone.getField(), "18538258111");
        shipMap.put(TcLogisticOrderFieldEnum.post.getField(), "123654");
        UpdateFieldRequest logFieldRequest = new UpdateFieldRequest();
        UpdateFieldParam logFieldParam = new UpdateFieldParam();
        logFieldParam.setLogisticOrderFields(shipMap);
        logFieldParam.setBizOrderId(createResponse.getBizOrderid());
        logFieldRequest.setUpdateFieldParam(logFieldParam);
        defaultTpTestClient.execute(logFieldRequest);

        // 付款产生物流订单
        PayRequest payRequest = new PayRequest();
        PayParam payParam = new PayParam();
        payParam.setBizorderid(createResponse.getBizOrderid());
        payRequest.setPayParam(payParam);

        defaultTpTestClient.execute(payRequest);
        System.exit(1);
    }

}
