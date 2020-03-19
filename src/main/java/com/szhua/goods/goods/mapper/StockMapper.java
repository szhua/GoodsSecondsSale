package com.szhua.goods.goods.mapper;

import com.szhua.goods.goods.entity.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wb
 * @since 2020-03-18
 */
public interface StockMapper extends BaseMapper<Stock> {
    int  updateByOptimistic(Stock stock );
}
