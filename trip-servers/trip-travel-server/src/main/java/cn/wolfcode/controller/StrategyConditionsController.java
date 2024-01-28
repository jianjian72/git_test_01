package cn.wolfcode.controller;

import cn.wolfcode.domain.StrategyCondition;
import cn.wolfcode.service.IStrategyConditionService;
import cn.wolfcode.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/strategies/conditions")
public class StrategyConditionsController extends BaseController {

    @Autowired
    private IStrategyConditionService strategyConditionService;

    @GetMapping
    public R<?> index(int type) {
        List<StrategyCondition> list = strategyConditionService.queryByType(type);
        return R.ok(list);
    }
}
