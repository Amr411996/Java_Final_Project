package com.example.demo;

import com.example.demo.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    //RequestMapping(value = "/index")
    @GetMapping(value = "/index")
    public String welcome(Model model){
       model.addAttribute("Task1", Service.showOriginalDF());
        model.addAttribute("Task2", Service.showDFInfo());
        model.addAttribute("Task3", Service.Task3());
        model.addAttribute("Task4", Service.Task4());
        model.addAttribute("Task6", Service.Task6());
        model.addAttribute("Task8", Service.Task8());
        model.addAttribute("Task10", Service.Task10());
        model.addAttribute("Task12", Service.Task12());

        //SparkDF.dataCleaning();
        return "index";
    }
}
