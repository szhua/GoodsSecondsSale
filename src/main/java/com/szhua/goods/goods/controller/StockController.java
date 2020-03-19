package com.szhua.goods.goods.controller;


import com.szhua.goods.goods.RedisUtil;
import com.szhua.goods.goods.entity.Stock;
import com.szhua.goods.goods.service.IStockOrderService;
import com.szhua.goods.goods.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wb
 * @since 2020-03-18
 */
@RestController
@RequestMapping("/stock")
@Slf4j
public class StockController {


    @Autowired
    IStockOrderService orderService ;

    @Autowired
    IStockService stockService ;

    @Autowired
    RedisUtil redisUtil ;

    @RequestMapping("/createWrongOrder/{sid}")
    @ResponseBody
    public String createWrongOrder(@PathVariable int sid) {
        log.info("购买物品编号sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createWrongOrder(sid);
            log.info("创建订单id: [{}]", id);
        } catch (Exception e) {
            log.error("购买失败：[{}]", e.getMessage());
            return "购买失败，库存不足";
        }
        return String.valueOf(id);
    }


    /**
     * 更改售卖的数量
     */
    /**
     * 增加库存；
     * @param sid
     * @return
     */
    @RequestMapping("/changeSaleCout/{sid}")
    @ResponseBody
    public String changeSaleCout(@PathVariable int sid ,int count ){
        //更改数据库；
        Stock stock =  stockService.getById(sid) ;
        stock.setSale(count);
        stockService.updateById(stock);

        //设置缓存
        redisUtil.set("stock:"+sid, String.valueOf(count));

        return   "设置完成";
    }



    /**
     * 增加库存；
     * @param sid
     * @return
     */
    @RequestMapping("/changeData/{sid}")
    @ResponseBody
    public String changeData(@PathVariable int sid ){
        //更改数据库；
        Stock stock =  stockService.getById(sid) ;
        stock.setCount(stock.getCount()+100);
        stockService.updateById(stock);

        //设置缓存
        redisUtil.set("stock:count:"+sid, String.valueOf(stock.getCount()));
        return   "设置完成";
    }
}
