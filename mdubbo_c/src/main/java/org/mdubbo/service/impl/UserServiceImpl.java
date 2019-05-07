package org.mdubbo.service.impl;

import telrob.mdubbo.service.UserService;

public class UserServiceImpl implements UserService {

	public String say(String text) {
		return text;
	}

	public void test(String name) {
		// TODO Auto-generated method stub

	}

	public void test(String name, int age) {
		System.out.println(name+":"+age);
	}

}
