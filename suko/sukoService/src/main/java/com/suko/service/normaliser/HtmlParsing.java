package com.suko.service.normaliser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Jordane
 *
 */
public class HtmlParsing {

	private static final Logger logger = LogManager.getLogger(HtmlParsing.class);
	private static final String tempFolderPath = System.getProperty("java.io.tmpdir");
	private static final String tempFileSuffix = "_Viadeo.htm";

	public static Document getFromFileOrUrl(String url, String name) {
		Document document = null;

		File tempFile = new File(tempFolderPath + name + tempFileSuffix);
		logger.trace("tempFile path: '" + tempFile.getAbsolutePath() + "'");

		if (!tempFile.exists()) {
			logger.debug("tempFile doesn't exists");
			document = getFromUrl(url);

			try {
				FileUtils.writeStringToFile(tempFile, document.outerHtml(), "UTF-8");
			} catch (IOException e) {
				logger.error("Impossible to save temp file", e);
			}
		} else {
			logger.debug("tempFile already exists");
			document = getFromFile(tempFile);
		}

		return document;
	}

	public static Document getFromUrl(String url) {
		logger.debug("getFromUrl IN");
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (Exception e) {
			logger.debug("Impossible to getFromUrl '" + url + "'", e);
		}

		return document;
	}

	public static Document getFromFile(String pathFile, String baseUri) {
		logger.debug("getFromFile IN");
		Document document = null;
		try {
			File file = new File(pathFile);
			document = Jsoup.parse(file, "UTF-8", baseUri);
		} catch (Exception e) {
			logger.debug("Impossible to getFromFile '" + pathFile + "'", e);
		}

		return document;
	}

	public static Document getFromFile(File htmlFile) {
		logger.debug("getFromFile IN");
		Document document = null;
		try {
			document = Jsoup.parse(htmlFile, "UTF-8", StringUtils.EMPTY);
		} catch (Exception e) {
			logger.error("Impossible to getFromFile '" + htmlFile + "'", e);
			
		}

		return document;
	}
}
