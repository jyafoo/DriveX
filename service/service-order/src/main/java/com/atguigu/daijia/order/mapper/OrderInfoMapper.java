package com.atguigu.daijia.order.mapper;

import com.atguigu.daijia.model.entity.order.OrderInfo;
import com.atguigu.daijia.model.vo.order.OrderListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


    /**
     * 查询指定客户的订单分页列表
     *
     * @param pageParam 分页参数，用于指定分页行为的页面大小和当前页码
     * @param customerId 客户的唯一标识ID
     * @return 返回一个分页对象，包含订单列表和分页信息此方法用于在客户相关操作中检索订单信息，通过客户ID进行过滤，同时支持分页显示
     */
    IPage<OrderListVo> selectCustomerOrderPage(Page<OrderInfo> pageParam, Long customerId);
}
