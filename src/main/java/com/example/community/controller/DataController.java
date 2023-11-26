package com.example.community.controller;

import com.example.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-11-25 16:23
 **/

@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    @RequestMapping(path = "/data", method = {RequestMethod.GET,RequestMethod.POST})
    public String getDatePage() {
        return "/site/admin/data";
    }

    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model){
        long uv = dataService.calendarUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);

        return "/site/admin/data";
    }

    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd")Date end,
                         Model model){
        long dau = dataService.calendarDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);

        return "/site/admin/data";

    }







}
