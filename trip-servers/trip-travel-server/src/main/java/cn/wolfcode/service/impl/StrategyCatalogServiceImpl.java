package cn.wolfcode.service.impl;

import cn.wolfcode.domain.Strategy;
import cn.wolfcode.domain.StrategyCatalog;
import cn.wolfcode.mapper.StrategyCatalogMapper;
import cn.wolfcode.query.BaseQuery;
import cn.wolfcode.service.IStrategyCatalogService;
import cn.wolfcode.service.IStrategyService;
import cn.wolfcode.vo.CatalogGroupVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StrategyCatalogServiceImpl extends ServiceImpl<StrategyCatalogMapper, StrategyCatalog> implements IStrategyCatalogService {

    @Autowired
    private IStrategyService strategyService;

    @Override
    public Page<StrategyCatalog> queryPage(BaseQuery qo) {
        // 条件构造器
        LambdaQueryWrapper<StrategyCatalog> wrapper = new LambdaQueryWrapper<StrategyCatalog>()
                // like(condition, column, value)
                // 当 condition 成立时，才会将这个条件拼接在 sql 语句上
                .like(StringUtils.hasLength(qo.getKeyword()), StrategyCatalog::getName, qo.getKeyword());

        // 分页方法
        return super.page(new Page<>(qo.getCurrentPage(), qo.getPageSize()), wrapper);
    }

    @Override
    public List<CatalogGroupVO> groupList() {
        // 1. 查询到所有攻略分类对象（状态为 0）
        List<StrategyCatalog> catalogList =
                list(Wrappers.<StrategyCatalog>lambdaQuery()
                        .eq(StrategyCatalog::getState, StrategyCatalog.STATE_NORMAL));

        // 2. 对攻略分类进行分组操作，并转换为 CatalogGroupVO 对象
        Map<String, List<StrategyCatalog>> groupMap = catalogList.stream()
                .collect(Collectors.groupingBy(StrategyCatalog::getDestName)); // 按照 destName 进行分组
        // {成都: [{destName: 成都, id: 1, name: xxx}, {destName: 成都, id: 2, name: aaa}, {destName: 成都, id: 3, name: cccc}]}

        // 3. 将分组后的 map 转换为 vo list 并返回
        List<CatalogGroupVO> voList = new ArrayList<>();
        for (Map.Entry<String, List<StrategyCatalog>> entry : groupMap.entrySet()) {
            CatalogGroupVO groupVO = new CatalogGroupVO();
            groupVO.setDestName(entry.getKey()); // Map 的 key 就是分组目的地的名称
            groupVO.setCatalogList(entry.getValue()); // Map 的 value 就是这个分组的所有分类

            voList.add(groupVO);
        }
        return voList;
    }

    @Override
    public List<StrategyCatalog> queryCatalogsByDestId(Long destId) {
        // 1. 基于目的地 id 查询分类列表
        List<StrategyCatalog> catalogs = list(new LambdaQueryWrapper<StrategyCatalog>().eq(StrategyCatalog::getDestId, destId));
        // 2. 遍历分类列表, 基于分类 id 查询攻略列表
        for (StrategyCatalog catalog : catalogs) {
            List<Strategy> strategies =
                    strategyService.list(new LambdaQueryWrapper<Strategy>().eq(Strategy::getCatalogId, catalog.getId()));
            // 将当前分类的攻略信息保存
            catalog.setStrategies(strategies);
        }
        // 3. 返回分类列表
        return catalogs;
    }
}
