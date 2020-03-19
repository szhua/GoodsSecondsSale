package com.szhua.goods.goods.service;

import com.szhua.goods.goods.entity.Stock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wb
 * @since 2020-03-18
 */
public interface IStockService extends IService<Stock> {

    int updateStockByOptimistic(Stock stock);
}
