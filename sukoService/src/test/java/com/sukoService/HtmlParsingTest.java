package com.sukoService;

import java.io.File;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.suko.service.normaliser.HtmlParsing;

public class HtmlParsingTest {

	@Test
	public void testGetFromFile() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		File htmlFile = new File(classLoader.getResource("html/JordaneQuincy_Viadeo.htm").getFile());

		Document document = HtmlParsing.getFromFile(htmlFile);
		Assert.assertNotNull(document);
	}

}
