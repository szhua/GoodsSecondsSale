package com.szhua.goods.goods.service;

import com.szhua.goods.goods.entity.StockOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wb
 * @since 2020-03-18
 */
public interface IStockOrderService extends IService<StockOrder> {

    int createWrongOrder(int sid);
}
