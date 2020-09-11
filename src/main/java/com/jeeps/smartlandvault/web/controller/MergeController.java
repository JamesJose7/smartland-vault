package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.nosql.merged_container.MergedContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MergeController {

    @Autowired
    private MergedContainerRepository mergedContainerRepository;

    @GetMapping("/merge")
    public String mergeMenu(Model model) {

        model.addAttribute("mergedContainers", mergedContainerRepository.findAll());
        model.addAttribute("newMergeLink", "/merge/new");
        return "merge/merge_home";
    }

}
