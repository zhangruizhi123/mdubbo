package org.mdubbo;

import telrob.mdubbo.ClassPathXmlContext;
import telrob.mdubbo.service.UserService;

public class App2 {
	public static void main(String[] args) {
		ClassPathXmlContext ctx=new ClassPathXmlContext("target/classes/consumer.xml",0);
		UserService user=ctx.getBean(UserService.class);
		String text=user.say("hello");
		user.test("张三", 22);
		System.out.println(text);
	}
}
