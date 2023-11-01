package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Description Freemarker测试
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/1
 */
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model) {
        //1.纯文本形式的参数
        model.addAttribute("name", "freemarker");
        //2.实体类相关的参数

        Student student = new Student();
        student.setName("小明");
        student.setAge(18);
        model.addAttribute("stu", student);

        return "basic";
    }

}
