package com.shareday.calendar.controller;

import com.shareday.calendar.service.SharedayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SharedayController {

    @Autowired
    private SharedayService sharedayService;

    @GetMapping("/")
    public String getSharedayPage(Model model) {
        String currentDate = sharedayService.getCurrentDate();
        model.addAttribute("currentDate", currentDate);
        return "index"; // index.html 템플릿을 반환
    }
}
