package cn.wolfcode.controller;

import cn.wolfcode.domain.StrategyRank;
import cn.wolfcode.service.IStrategyRankService;
import cn.wolfcode.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/strategies/ranks")
public class StrategyRankController extends BaseController {

    @Autowired
    private IStrategyRankService strategyRankService;

    @GetMapping
    public R<?> index(int type) {
        List<StrategyRank> list = strategyRankService.queryByType(type);
        return R.ok(list);
    }
}
