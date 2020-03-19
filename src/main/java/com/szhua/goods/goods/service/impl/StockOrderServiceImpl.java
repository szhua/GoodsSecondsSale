package com.szhua.goods.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.szhua.goods.goods.RedisUtil;
import com.szhua.goods.goods.entity.Stock;
import com.szhua.goods.goods.entity.StockOrder;
import com.szhua.goods.goods.mapper.StockOrderMapper;
import com.szhua.goods.goods.service.IStockOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szhua.goods.goods.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wb
 * @since 2020-03-18
 */
@Slf4j
@Service
public class StockOrderServiceImpl extends ServiceImpl<StockOrderMapper, StockOrder> implements IStockOrderService {


    @Autowired
    IStockService stockService ;


    @Autowired
    RedisUtil redisUtil ;


//    private Map<Integer,Boolean> cache =new ConcurrentHashMap<>();


    @Autowired
    ValueOperations<String ,Object> valueOperations ;



    @Override
    public int createWrongOrder(int sid)  {
//        if (!cache.containsKey(sid)){
//             cache.put(sid,true);
//        }
//        if (!cache.get(sid)){
//            throw new RuntimeException("缓存库存检验库存已售完！");
//        }


        //校验库存
        Stock stock = checkStock(sid);


        //数据库进行更新；
        saleStockOptimistic(stock);


        //创建订单
        int orderId = createOrder(stock);
        if(orderId>0) {
            log.info("购买成功:订单编号{}",orderId);
        }
        return  orderId ;

    }

    @Transactional
    public void saleStockOptimistic(Stock stock) {

        if (new Random().nextInt(1000)%3==0){
            //恢复增加的库存；
            redisUtil.incrBy("stock:" + stock.getId(),-1);
            throw  new RuntimeException("我试着抛出了异常");
        }
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0){
            redisUtil.incrBy("stock:" + stock.getId(),-1);
              throw  new RuntimeException("数据库锁机制，库存已经没有了！");
        }

    }

    private Stock checkStock(int sid) {


        Stock stock =new Stock();

        String stockCount =redisUtil.get("stock:count:"+sid);

        //redis预备减库存；
        Integer  cacheNumber  = Math.toIntExact(redisUtil.incrBy("stock:" + sid, 1));
        log.info("cacheNumber:{}",cacheNumber);


        if (StringUtils.isEmpty(stockCount)){
            stock = stockService.getById(sid);
            stockCount = String.valueOf(stock.getCount());
            redisUtil.set("stock:count:"+sid, stockCount);
        }


        log.error("redis缓存库存 {},stockCout:{}",cacheNumber,stockCount);

        if (StringUtils.isEmpty(stockCount)){
            stock = stockService.getById(sid);
            if (stock.getSale()>=stock.getCount()) {
                redisUtil.incrBy("stock:" + sid,-1);
                throw  new RuntimeException("mysql检查库存售完");
            }
            redisUtil.set("stock:count:"+sid, String.valueOf(stock.getCount()));
        }else{
            stock.setSale(cacheNumber);
            stock.setCount(Integer.valueOf(stockCount));
            stock.setId(sid);
            if (stock.getSale()>stock.getCount()) {
                //恢复增加的库存；
                redisUtil.incrBy("stock:" + sid,-1);
                throw  new RuntimeException("redis检查库存已售完");
            }
        }


        return stock;
    }



    private int createOrder(Stock stock) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        save(order);
        return order.getId();
    }


}
