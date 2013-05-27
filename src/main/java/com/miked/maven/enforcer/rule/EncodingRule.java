package com.miked.maven.enforcer.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * Checks file encodings to see if they match the project.build.sourceEncoding
 * 
 * If file encoding can not be determined it is skipped.
 * 
 * @author miked
 */
public class EncodingRule implements EnforcerRule {
	/**
	 * Directory to search for files
	 */
	private String directory = "";

	/**
	 * Regular Expression to match file names against
	 */
	private String includes = "";

	/**
	 * Validate files match this encoding. If not specified then default to
	 * ${project.builder.sourceEncoding}.
	 */
	private String encoding = "";

	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		try {
			if (this.getEncoding() == null) {
				this.setEncoding((String) helper
					.evaluate("${project.build.sourceEncoding}"));
			}

			String target = (String) helper.evaluate("${basedir}");
			File dir = new File(target + System.getProperty("file.separator")
					+ getDirectory());
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(getIncludes());
				}
			});
			Map<String, String> filesInError = new HashMap<String, String>();
			String fileEncoding = null;
			for (File file : files) {
				fileEncoding = getEncoding(file);
				if (fileEncoding != null && !fileEncoding.equals(getEncoding())) {
					filesInError.put(file.getName(), fileEncoding);
				}
			}
			if (!filesInError.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("Files not encoded in ");
				builder.append(getEncoding());
				builder.append(":");
				builder.append("\n");
				for (Entry<String, String> entry : filesInError.entrySet()) {
					builder.append(entry.getKey());
					builder.append("==>");
					builder.append(entry.getValue());
					builder.append("\n");
				}
				throw new EnforcerRuleException(builder.toString());
			}
		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression "
					+ e.getLocalizedMessage(), e);
		}
	}

	protected String getEncoding(File file) {
		byte[] buf = new byte[4096];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			UniversalDetector detector = new UniversalDetector(null);
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			String encoding = detector.getDetectedCharset();
			detector.reset();
			return encoding;
		} catch (FileNotFoundException e) {
			// TODO
		} catch (IOException e) {
			// TODO
		}
		return null;
	}

	/**
	 * If your rule is cacheable, you must return a unique id when parameters or
	 * conditions change that would cause the result to be different. Multiple
	 * cached results are stored based on their id.
	 * 
	 * The easiest way to do this is to return a hash computed from the values
	 * of your parameters.
	 * 
	 * If your rule is not cacheable, then the result here is not important, you
	 * may return anything.
	 */
	public String getCacheId() {
		// no hash on boolean...only parameter so no hash is needed.
		return "" + this.getDirectory() + this.getIncludes();
	}

	/**
	 * This tells the system if the results are cacheable at all. Keep in mind
	 * that during forked builds and other things, a given rule may be executed
	 * more than once for the same project. This means that even things that
	 * change from project to project may still be cacheable in certain
	 * instances.
	 */
	public boolean isCacheable() {
		return false;
	}

	/**
	 * If the rule is cacheable and the same id is found in the cache, the
	 * stored results are passed to this method to allow double checking of the
	 * results. Most of the time this can be done by generating unique ids, but
	 * sometimes the results of objects returned by the helper need to be
	 * queried. You may for example, store certain objects in your rule and then
	 * query them later.
	 */
	public boolean isResultValid(EnforcerRule arg0) {
		return false;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
