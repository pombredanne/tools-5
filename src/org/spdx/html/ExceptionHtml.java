/**
 * Copyright (c) 2014 Source Auditor Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package org.spdx.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.spdx.licenseTemplate.SpdxLicenseTemplateHelper;
import org.spdx.rdfparser.license.LicenseException;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.MustacheException;

/**
 * Manages the production of an HTML file based on an SpdxLicenseRestriction (a.k.a License Exception)
 * @author Gary O'Neall
 *
 */
public class ExceptionHtml {
	
	static final String TEMPLATE_CLASS_PATH = "resources" + "/" + "htmlTemplate";
	static final String TEMPLATE_ROOT_PATH = "resources" + File.separator + "htmlTemplate";
	static final String HTML_TEMPLATE = "ExceptionHTMLTemplate.html";
	
	HashMap<String, Object> mustacheMap = new HashMap<String, Object>();
	/**
	 * @param exception
	 */
	public ExceptionHtml(LicenseException exception) {
		ArrayList<String> alSourceUrls = new ArrayList<String>();
		String[] sourceUrls = exception.getSeeAlso();
		if (sourceUrls != null) {
			for (String sourceUrl: sourceUrls) {
				alSourceUrls.add(sourceUrl);
			}
		}
		mustacheMap.put("name", exception.getName());
		mustacheMap.put("id", exception.getLicenseExceptionId());
		mustacheMap.put("text", SpdxLicenseTemplateHelper.escapeHTML(exception.getLicenseExceptionText()));
		mustacheMap.put("getSourceUrl", alSourceUrls);
		mustacheMap.put("notes", exception.getComment());
		String example = exception.getExample();
		if (example != null && example.trim().isEmpty()) {
			example = null;
		}
		mustacheMap.put("example", example);
	}

	/**
	 * @param exceptionHtmlFile
	 * @param exceptionHtmlTocReference
	 * @throws IOException 
	 * @throws MustacheException 
	 */
	public void writeToFile(File exceptionHtmlFile,
			String exceptionHtmlTocReference) throws IOException, MustacheException {
		mustacheMap.put("exceptionTocReference", exceptionHtmlTocReference);
		FileOutputStream stream = null;
		OutputStreamWriter writer = null;
		if (!exceptionHtmlFile.exists()) {
			if (!exceptionHtmlFile.createNewFile()) {
				throw(new IOException("Can not create new file "+exceptionHtmlFile.getName()));
			}
		}
		String templateDirName = TEMPLATE_ROOT_PATH;
		File templateDirectoryRoot = new File(templateDirName);
		if (!(templateDirectoryRoot.exists() && templateDirectoryRoot.isDirectory())) {
			templateDirName = TEMPLATE_CLASS_PATH;
		}
		try {
			stream = new FileOutputStream(exceptionHtmlFile);
			writer = new OutputStreamWriter(stream, "UTF-8");
	        MustacheBuilder builder = new MustacheBuilder(templateDirName);
	        Mustache mustache = builder.parseFile(HTML_TEMPLATE);
	        mustache.execute(writer, mustacheMap);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (stream != null) {
				stream.close();
			}
		}
	}

}
