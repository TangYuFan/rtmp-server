package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SpringBootApplication
@Controller
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@RequestMapping(value="/index.html", method= RequestMethod.GET)
	public String test(HttpServletRequest request, HttpServletResponse response,ModelMap map) throws Exception{

		String src = request.getParameter("src");//rtmp://192.168.1.201/live/pushFlow
		System.out.println("src:	"+src);
		map.addAttribute("src",src);
		return "index";
	}

}
