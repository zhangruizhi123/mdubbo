package org.mdubbo;

import telrob.mdubbo.ClassPathXmlContext;

public class App2 {
	public static void main(String[] args) {
		ClassPathXmlContext ctx=new ClassPathXmlContext("target/classes/provider.xml",ClassPathXmlContext.PROVIDER);
	}
}
