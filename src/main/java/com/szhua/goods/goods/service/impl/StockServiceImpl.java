package com.szhua.goods.goods.service.impl;

import com.szhua.goods.goods.entity.Stock;
import com.szhua.goods.goods.mapper.StockMapper;
import com.szhua.goods.goods.service.IStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wb
 * @since 2020-03-18
 */
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    @Override
    public int updateStockByOptimistic(Stock stock) {
        int count =  baseMapper.updateByOptimistic(stock);
        return  count ;
    }
}
