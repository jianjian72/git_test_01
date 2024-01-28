package cn.wolfcode.service.impl;

import cn.wolfcode.domain.Destination;
import cn.wolfcode.domain.Region;
import cn.wolfcode.mapper.DestinationMapper;
import cn.wolfcode.query.DestinationQuery;
import cn.wolfcode.service.IDestinationService;
import cn.wolfcode.service.IRegionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DestinationServiceImpl extends ServiceImpl<DestinationMapper, Destination> implements IDestinationService {

    @Autowired
    private IRegionService regionService;

    @Override
    public Page<Destination> queryPage(DestinationQuery qo) {
        // 条件构造器
        LambdaQueryWrapper<Destination> wrapper = new LambdaQueryWrapper<Destination>()
                // like(condition, column, value)
                // 当 condition 成立时，才会将这个条件拼接在 sql 语句上
                .like(StringUtils.hasLength(qo.getKeyword()), Destination::getName, qo.getKeyword());

        if (qo.getParentId() == null) {
            // parent_id IS NULL
            wrapper.isNull(Destination::getParentId); // 当前端不传 parentId 过来时，就只查询 parentId 为 null 的数据
        } else {
            // parent_id = #{parentId}
            wrapper.eq(Destination::getParentId, qo.getParentId());
        }

        // 分页方法
        return super.page(new Page<>(qo.getCurrentPage(), qo.getPageSize()), wrapper);
    }

    @Override
    public List<Destination> queryToasts(Long destId) {
        // 1. 封装一个接口，接收一个目的地 id 参数
        List<Destination> list = new ArrayList<>();

        Long parentId = destId;
        do {
            // 2. 基于该 id 查询出对象，将该对象存入集合
            Destination dest = super.getById(parentId);
            if (dest == null) {
                parentId = null;
                break;
            }

            list.add(dest);

            // 3. 判断该对象是否还有父节点
            // 4. 如果有父节点，以父节点 id 作为 id 参数，重复第一步
            parentId = dest.getParentId();
        } while (parentId != null);

        // 5. 如果没有父节点，代表找了所有的父节点
        Collections.reverse(list); // 将已经得到的土司数据进行翻转
        return list;
    }

    @Override
    public List<Destination> queryByRegionId(Long rid) {
        // 最终返回的数据
        List<Destination> list = null;
        if (rid == -1) { // -1 代表查询国内
            // 查询国内的热门目的地
            list = super.list(new LambdaQueryWrapper<Destination>().eq(Destination::getParentId, 1));
        } else {
            // 查询热门区域
            Region region = regionService.getById(rid);

            // 查询该热门区域下的所有热门目的地
            List<Long> destIds = region.parseRefIds(); // 获取到区域对象管理的所有目的地 id
            list = super.listByIds(destIds);
        }

        // 查询所有目的地的子数据
        for (Destination parent : list) {
            List<Destination> children = this.listByParentId(parent.getId(), 10);
            parent.setChildren(children);
        }

        return list;
    }

    @Override
    public Destination findByName(String name) {
        return super.getOne(new LambdaQueryWrapper<Destination>()
                .eq(Destination::getName, name));
    }

    private List<Destination> listByParentId(Long parentId, int limit) {
        // SELECT * FROM destination WHERE parent_id = #{parentId}
        return list(Wrappers.<Destination>lambdaQuery()
                .eq(Destination::getParentId, parentId)
                .last("limit " + limit));
    }
}
