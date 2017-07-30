package com.happygo.dlc.api.controller;

import com.happygo.dlc.biz.service.LogSourceService;
import com.happygo.dlc.common.entity.LogSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: DlcLogSourceController <br/>
 * Description: DlcLogSourceController <br/>
 * Date: 2017/7/30 10:36 <br/>
 * Company: TravelSky <br/>
 *
 * @author sxp(sxp@travelsky.com) <br/>
 * @version 1.0 <br/>
 */
@RestController
@RequestMapping("/dlc")
public class DlcLogSourceController {

    @Autowired
    private LogSourceService logSourceService;

    @PostMapping(value = "/logsource/insert")
    public ModelAndView insert(LogSource logSource) {
        logSourceService.saveLogSource(logSource);
        return selectList();
    }

    @GetMapping(value = "/logsource/select/list")
    public ModelAndView selectList() {
        ModelAndView modelAndView = new ModelAndView("logsource_list");
        List<LogSource> logSourceList = logSourceService.selectList();
        if (logSourceList == null) {
            modelAndView.addObject("logSourceList", new ArrayList<>(0));
        }
        modelAndView.addObject("logSourceList", logSourceList);
        return modelAndView;
    }

    @GetMapping(value = "/logsource/delete")
    public ModelAndView deleteLogSource(@RequestParam("id") int id) {
        logSourceService.deleteLogSource(id);
        return selectList();
    }
}