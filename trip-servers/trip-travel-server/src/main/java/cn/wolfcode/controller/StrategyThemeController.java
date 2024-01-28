package cn.wolfcode.controller;

import cn.wolfcode.domain.StrategyTheme;
import cn.wolfcode.query.BaseQuery;
import cn.wolfcode.service.IStrategyThemeService;
import cn.wolfcode.vo.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strategyThemes")
public class StrategyThemeController {

    @Autowired
    private IStrategyThemeService strategyThemeService;

    @GetMapping("/query")
    public R<?> query(BaseQuery qo) {
        Page<StrategyTheme> page = strategyThemeService.queryPage(qo);
        return R.ok(page);
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(strategyThemeService.list());
    }

    @GetMapping("/detail")
    public R<?> detail(Long id) {
        return R.ok(strategyThemeService.getById(id));
    }

    @PostMapping("/save")
    public R<?> save(StrategyTheme StrategyTheme) {
        strategyThemeService.save(StrategyTheme);
        return R.ok();
    }

    @PostMapping("/update")
    public R<?> update(StrategyTheme StrategyTheme) {
        strategyThemeService.updateById(StrategyTheme);
        return R.ok();
    }

    @PostMapping("/delete/{id}")
    public R<?> delete(@PathVariable Long id) {
        if (id != null) {
            strategyThemeService.removeById(id);
        }
        return R.ok();
    }

}
